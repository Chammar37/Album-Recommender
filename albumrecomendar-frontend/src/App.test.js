import { render, screen, fireEvent, waitFor } from '@testing-library/svelte';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from './App.svelte';

describe('App Component', () => {
    const mockAlbums = [
        {
            id: 'album1',
            name: 'Album One',
            artists: [{ id: 'a1', name: 'Artist One' }],
            images: [{ url: 'https://example.com/1.jpg' }]
        },
        {
            id: 'album2',
            name: 'Album Two',
            artists: [{ id: 'a2', name: 'Artist Two' }],
            images: [{ url: 'https://example.com/2.jpg' }]
        }
    ];

    beforeEach(() => {
        vi.clearAllMocks();
    });

    describe('Initial Load', () => {
        it('should show loading state initially', () => {
            global.fetch = vi.fn(() => new Promise(() => {})); // Never resolves

            render(App);

            expect(screen.getByText('Finding albums...')).toBeInTheDocument();
        });

        it('should fetch initial recommendations on mount', async () => {
            global.fetch = vi.fn().mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve(mockAlbums)
            });

            render(App);

            await waitFor(() => {
                expect(global.fetch).toHaveBeenCalledWith(
                    expect.stringContaining('/recommendations')
                );
            });
        });

        it('should display albums after successful fetch', async () => {
            global.fetch = vi.fn().mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve(mockAlbums)
            });

            render(App);

            await waitFor(() => {
                expect(screen.getByAltText('Album One by Artist One')).toBeInTheDocument();
                expect(screen.getByAltText('Album Two by Artist Two')).toBeInTheDocument();
            });
        });

        it('should show error message on fetch failure', async () => {
            global.fetch = vi.fn().mockResolvedValueOnce({
                ok: false,
                status: 500
            });

            render(App);

            await waitFor(() => {
                expect(screen.getByText(/Failed to load recommendations/)).toBeInTheDocument();
            });
        });

        it('should show error message on network error', async () => {
            global.fetch = vi.fn().mockRejectedValueOnce(new Error('Network error'));

            render(App);

            await waitFor(() => {
                expect(screen.getByText(/Failed to load recommendations/)).toBeInTheDocument();
            });
        });
    });

    describe('Search Functionality', () => {
        beforeEach(() => {
            // Initial load succeeds
            global.fetch = vi.fn().mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve(mockAlbums)
            });
        });

        it('should have search input and button', async () => {
            render(App);

            await waitFor(() => {
                expect(screen.getByPlaceholderText(/Search for an album/)).toBeInTheDocument();
                expect(screen.getByRole('button', { name: 'Search' })).toBeInTheDocument();
            });
        });

        it('should search for albums when form is submitted', async () => {
            const searchResults = [
                {
                    id: 'search1',
                    name: 'Search Result',
                    artists: [{ id: 's1', name: 'Search Artist' }],
                    images: [{ url: 'https://example.com/search.jpg' }]
                }
            ];

            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(searchResults)
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByPlaceholderText(/Search for an album/)).toBeInTheDocument();
            });

            const input = screen.getByPlaceholderText(/Search for an album/);
            const searchButton = screen.getByRole('button', { name: 'Search' });

            await fireEvent.input(input, { target: { value: 'Test Query' } });
            await fireEvent.click(searchButton);

            await waitFor(() => {
                expect(global.fetch).toHaveBeenCalledWith(
                    expect.stringContaining('/search?query=Test%20Query')
                );
            });
        });

        it('should not search with empty query', async () => {
            render(App);

            await waitFor(() => {
                expect(screen.getByRole('button', { name: 'Search' })).toBeInTheDocument();
            });

            const searchButton = screen.getByRole('button', { name: 'Search' });
            await fireEvent.click(searchButton);

            // Should only have the initial fetch call
            expect(global.fetch).toHaveBeenCalledTimes(1);
        });

        it('should show error on search failure', async () => {
            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                })
                .mockResolvedValueOnce({
                    ok: false,
                    status: 500
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByPlaceholderText(/Search for an album/)).toBeInTheDocument();
            });

            const input = screen.getByPlaceholderText(/Search for an album/);
            const searchButton = screen.getByRole('button', { name: 'Search' });

            await fireEvent.input(input, { target: { value: 'Test' } });
            await fireEvent.click(searchButton);

            await waitFor(() => {
                expect(screen.getByText(/Search failed/)).toBeInTheDocument();
            });
        });
    });

    describe('Similar Albums (Discovery Flow)', () => {
        it('should fetch similar albums when album is clicked', async () => {
            const similarAlbums = [
                {
                    id: 'similar1',
                    name: 'Similar Album',
                    artists: [{ id: 'sim1', name: 'Similar Artist' }],
                    images: [{ url: 'https://example.com/similar.jpg' }]
                }
            ];

            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(similarAlbums)
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByAltText('Album One by Artist One')).toBeInTheDocument();
            });

            // Click on the first album
            const albumCard = screen.getByAltText('Album One by Artist One').closest('[role="button"]');
            await fireEvent.click(albumCard);

            await waitFor(() => {
                expect(global.fetch).toHaveBeenCalledWith(
                    expect.stringContaining('/similar?albumId=album1')
                );
            });
        });

        it('should display similar albums after clicking', async () => {
            const similarAlbums = [
                {
                    id: 'similar1',
                    name: 'Similar Album',
                    artists: [{ id: 'sim1', name: 'Similar Artist' }],
                    images: [{ url: 'https://example.com/similar.jpg' }]
                }
            ];

            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(similarAlbums)
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByAltText('Album One by Artist One')).toBeInTheDocument();
            });

            const albumCard = screen.getByAltText('Album One by Artist One').closest('[role="button"]');
            await fireEvent.click(albumCard);

            await waitFor(() => {
                expect(screen.getByAltText('Similar Album by Similar Artist')).toBeInTheDocument();
            });
        });

        it('should show error when similar albums fetch fails', async () => {
            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                })
                .mockResolvedValueOnce({
                    ok: false,
                    status: 500
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByAltText('Album One by Artist One')).toBeInTheDocument();
            });

            const albumCard = screen.getByAltText('Album One by Artist One').closest('[role="button"]');
            await fireEvent.click(albumCard);

            await waitFor(() => {
                expect(screen.getByText(/Failed to find similar albums/)).toBeInTheDocument();
            });
        });
    });

    describe('Reset Functionality', () => {
        it('should reset to initial recommendations when logo is clicked', async () => {
            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByText('Album Recommender')).toBeInTheDocument();
            });

            const logo = screen.getByText('Album Recommender');
            await fireEvent.click(logo);

            await waitFor(() => {
                expect(global.fetch).toHaveBeenCalledTimes(2);
            });
        });

        it('should reset when retry button is clicked after error', async () => {
            global.fetch = vi.fn()
                .mockResolvedValueOnce({
                    ok: false,
                    status: 500
                })
                .mockResolvedValueOnce({
                    ok: true,
                    json: () => Promise.resolve(mockAlbums)
                });

            render(App);

            await waitFor(() => {
                expect(screen.getByText('Try Again')).toBeInTheDocument();
            });

            const retryButton = screen.getByText('Try Again');
            await fireEvent.click(retryButton);

            await waitFor(() => {
                expect(global.fetch).toHaveBeenCalledTimes(2);
            });
        });
    });

    describe('Loading States', () => {
        it('should show spinner during loading', () => {
            global.fetch = vi.fn(() => new Promise(() => {}));

            render(App);

            expect(document.querySelector('.spinner')).toBeInTheDocument();
        });

        it('should disable search button while loading', () => {
            global.fetch = vi.fn(() => new Promise(() => {}));

            render(App);

            const searchButton = screen.getByRole('button', { name: 'Search' });
            expect(searchButton).toBeDisabled();
        });
    });

    describe('Empty States', () => {
        it('should show empty state when no albums returned', async () => {
            global.fetch = vi.fn().mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve([])
            });

            render(App);

            await waitFor(() => {
                expect(screen.getByText(/No albums found/)).toBeInTheDocument();
            });
        });
    });

    describe('UI Elements', () => {
        beforeEach(() => {
            global.fetch = vi.fn().mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve(mockAlbums)
            });
        });

        it('should display app title', async () => {
            render(App);

            await waitFor(() => {
                expect(screen.getByText('Album Recommender')).toBeInTheDocument();
            });
        });

        it('should display tagline', async () => {
            render(App);

            await waitFor(() => {
                expect(screen.getByText(/Click an album to discover sonically similar music/)).toBeInTheDocument();
            });
        });

        it('should render album grid with correct class', async () => {
            render(App);

            await waitFor(() => {
                expect(document.querySelector('.album-grid')).toBeInTheDocument();
            });
        });
    });
});
