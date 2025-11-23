import { render, screen, fireEvent } from '@testing-library/svelte';
import { describe, it, expect, vi } from 'vitest';
import Album from './Album.svelte';

describe('Album Component', () => {
    const mockAlbum = {
        id: 'album123',
        name: 'Test Album',
        artists: [{ id: 'artist1', name: 'Test Artist' }],
        images: [{ url: 'https://example.com/cover.jpg' }]
    };

    describe('Rendering', () => {
        it('should render album cover image', () => {
            render(Album, { props: mockAlbum });

            const img = screen.getByRole('img');
            expect(img).toBeInTheDocument();
            expect(img).toHaveAttribute('src', 'https://example.com/cover.jpg');
            expect(img).toHaveAttribute('alt', 'Test Album by Test Artist');
        });

        it('should render album name in info overlay', () => {
            render(Album, { props: mockAlbum });

            expect(screen.getByText('Test Album')).toBeInTheDocument();
        });

        it('should render artist name in info overlay', () => {
            render(Album, { props: mockAlbum });

            expect(screen.getByText('Test Artist')).toBeInTheDocument();
        });

        it('should render placeholder when no image provided', () => {
            render(Album, {
                props: {
                    ...mockAlbum,
                    images: []
                }
            });

            expect(screen.getByText('No Image')).toBeInTheDocument();
        });

        it('should show "Unknown Artist" when no artists provided', () => {
            render(Album, {
                props: {
                    ...mockAlbum,
                    artists: []
                }
            });

            expect(screen.getByText('Unknown Artist')).toBeInTheDocument();
        });

        it('should have proper accessibility attributes', () => {
            render(Album, { props: mockAlbum });

            const button = screen.getByRole('button');
            expect(button).toHaveAttribute('tabindex', '0');
            expect(button).toHaveAttribute('aria-label', 'Find albums similar to Test Album by Test Artist');
        });
    });

    describe('Interactions', () => {
        it('should dispatch albumselect event on click', async () => {
            const { component } = render(Album, { props: mockAlbum });

            const dispatchedEvents = [];
            component.$on('albumselect', (e) => dispatchedEvents.push(e.detail));

            const button = screen.getByRole('button');
            await fireEvent.click(button);

            expect(dispatchedEvents).toHaveLength(1);
            expect(dispatchedEvents[0]).toEqual({
                id: 'album123',
                name: 'Test Album',
                artists: [{ id: 'artist1', name: 'Test Artist' }],
                images: [{ url: 'https://example.com/cover.jpg' }]
            });
        });

        it('should dispatch albumselect event on Enter key', async () => {
            const { component } = render(Album, { props: mockAlbum });

            const dispatchedEvents = [];
            component.$on('albumselect', (e) => dispatchedEvents.push(e.detail));

            const button = screen.getByRole('button');
            await fireEvent.keyDown(button, { key: 'Enter' });

            expect(dispatchedEvents).toHaveLength(1);
        });

        it('should dispatch albumselect event on Space key', async () => {
            const { component } = render(Album, { props: mockAlbum });

            const dispatchedEvents = [];
            component.$on('albumselect', (e) => dispatchedEvents.push(e.detail));

            const button = screen.getByRole('button');
            await fireEvent.keyDown(button, { key: ' ' });

            expect(dispatchedEvents).toHaveLength(1);
        });

        it('should not dispatch event on other keys', async () => {
            const { component } = render(Album, { props: mockAlbum });

            const dispatchedEvents = [];
            component.$on('albumselect', (e) => dispatchedEvents.push(e.detail));

            const button = screen.getByRole('button');
            await fireEvent.keyDown(button, { key: 'Tab' });

            expect(dispatchedEvents).toHaveLength(0);
        });
    });

    describe('Edge Cases', () => {
        it('should handle null artists gracefully', () => {
            render(Album, {
                props: {
                    ...mockAlbum,
                    artists: null
                }
            });

            expect(screen.getByText('Unknown Artist')).toBeInTheDocument();
        });

        it('should handle null images gracefully', () => {
            render(Album, {
                props: {
                    ...mockAlbum,
                    images: null
                }
            });

            expect(screen.getByText('No Image')).toBeInTheDocument();
        });

        it('should handle missing props with defaults', () => {
            render(Album, {
                props: {
                    id: 'test-id',
                    name: 'Test'
                }
            });

            expect(screen.getByText('Test')).toBeInTheDocument();
            expect(screen.getByText('Unknown Artist')).toBeInTheDocument();
        });
    });

    describe('Styling', () => {
        it('should have album class on container', () => {
            render(Album, { props: mockAlbum });

            const container = screen.getByRole('button');
            expect(container).toHaveClass('album');
        });

        it('should have album-cover class on image', () => {
            render(Album, { props: mockAlbum });

            const img = screen.getByRole('img');
            expect(img).toHaveClass('album-cover');
        });
    });
});
