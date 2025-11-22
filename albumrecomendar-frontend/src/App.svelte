<script>
    import Album from './Album.svelte';
    import { onMount } from "svelte";

    const API_BASE = 'http://localhost:8080';

    let albums = [];
    let searchQuery = '';
    let isLoading = false;
    let error = null;
    let currentView = 'initial'; // 'initial', 'search', 'similar'

    // Load initial recommendations on mount
    onMount(async () => {
        await loadInitialRecommendations();
    });

    async function loadInitialRecommendations() {
        isLoading = true;
        error = null;
        try {
            const response = await fetch(
                `${API_BASE}/recommendations?seedArtist=2ANVost0y2y52ema1E9xAZ&seedGenres=hip-hop&seedTracks=0c6xIDDpzE81m2q797ordA&targetEnergy=0.7&targetDanceability=0.7&targetValence=0.1`
            );
            if (!response.ok) throw new Error('Failed to load recommendations');
            albums = await response.json();
            currentView = 'initial';
        } catch (e) {
            error = 'Failed to load recommendations. Please try again.';
            console.error(e);
        } finally {
            isLoading = false;
        }
    }

    // Search for albums by query
    async function handleSearch(event) {
        event.preventDefault();
        if (!searchQuery.trim()) return;

        isLoading = true;
        error = null;
        try {
            const response = await fetch(`${API_BASE}/search?query=${encodeURIComponent(searchQuery)}`);
            if (!response.ok) throw new Error('Search failed');
            albums = await response.json();
            currentView = 'search';
        } catch (e) {
            error = 'Search failed. Please try again.';
            console.error(e);
        } finally {
            isLoading = false;
        }
    }

    // Find sonically similar albums when an album is clicked
    async function handleAlbumClick(event) {
        const album = event.detail;

        isLoading = true;
        error = null;
        try {
            const response = await fetch(`${API_BASE}/similar?albumId=${album.id}`);
            if (!response.ok) throw new Error('Failed to find similar albums');
            albums = await response.json();
            currentView = 'similar';
        } catch (e) {
            error = 'Failed to find similar albums. Please try again.';
            console.error(e);
        } finally {
            isLoading = false;
        }
    }

    // Reset to initial view
    function handleReset() {
        searchQuery = '';
        loadInitialRecommendations();
    }
</script>

<main>
    <header>
        <h1 on:click={handleReset} class="logo">Album Recommender</h1>
        <p class="tagline">Click an album to discover sonically similar music</p>

        <form on:submit={handleSearch} class="search-form">
            <input
                type="text"
                bind:value={searchQuery}
                placeholder="Search for an album or artist..."
                class="search-input"
            />
            <button type="submit" class="search-button" disabled={isLoading}>
                Search
            </button>
        </form>
    </header>

    {#if error}
        <div class="error-message">
            {error}
            <button on:click={handleReset} class="retry-button">Try Again</button>
        </div>
    {/if}

    {#if isLoading}
        <div class="loading">
            <div class="spinner"></div>
            <p>Finding albums...</p>
        </div>
    {:else if albums.length > 0}
        <div class="album-grid">
            {#each albums as album (album.id)}
                <Album {...album} on:albumselect={handleAlbumClick} />
            {/each}
        </div>
    {:else}
        <div class="empty-state">
            <p>No albums found. Try a different search.</p>
        </div>
    {/if}
</main>

<style>
    main {
        max-width: 1400px;
        margin: 0 auto;
        padding: 20px;
    }

    header {
        text-align: center;
        margin-bottom: 30px;
    }

    .logo {
        font-size: 2rem;
        margin: 0 0 8px 0;
        cursor: pointer;
        transition: opacity 0.2s;
    }

    .logo:hover {
        opacity: 0.8;
    }

    .tagline {
        color: #888;
        margin: 0 0 20px 0;
        font-size: 0.95rem;
    }

    .search-form {
        display: flex;
        justify-content: center;
        gap: 10px;
        max-width: 500px;
        margin: 0 auto;
    }

    .search-input {
        flex: 1;
        padding: 12px 16px;
        font-size: 1rem;
        border: 1px solid #444;
        border-radius: 8px;
        background: #333;
        color: #fff;
        outline: none;
        transition: border-color 0.2s;
    }

    .search-input:focus {
        border-color: #646cff;
    }

    .search-input::placeholder {
        color: #777;
    }

    .search-button {
        padding: 12px 24px;
        font-size: 1rem;
        background: #646cff;
        color: white;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        transition: background 0.2s;
    }

    .search-button:hover:not(:disabled) {
        background: #535bf2;
    }

    .search-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .album-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
        gap: 16px;
    }

    .loading {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 60px 20px;
        color: #888;
    }

    .spinner {
        width: 40px;
        height: 40px;
        border: 3px solid #333;
        border-top-color: #646cff;
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin-bottom: 16px;
    }

    @keyframes spin {
        to { transform: rotate(360deg); }
    }

    .error-message {
        background: #ff4444;
        color: white;
        padding: 16px;
        border-radius: 8px;
        text-align: center;
        margin-bottom: 20px;
    }

    .retry-button {
        margin-left: 12px;
        padding: 8px 16px;
        background: white;
        color: #ff4444;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    .empty-state {
        text-align: center;
        padding: 60px 20px;
        color: #888;
    }

    @media (max-width: 600px) {
        .search-form {
            flex-direction: column;
        }

        .album-grid {
            grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
            gap: 12px;
        }
    }
</style>
