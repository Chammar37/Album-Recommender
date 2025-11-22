<script>
    import { createEventDispatcher } from 'svelte';

    export let id;
    export let name;
    export let artists = [];
    export let images = [];

    const dispatch = createEventDispatcher();

    // Get artist name safely
    $: artistName = artists && artists.length > 0 ? artists[0].name : 'Unknown Artist';

    // Get cover image safely
    $: coverUrl = images && images.length > 0 ? images[0].url : '';

    function handleClick() {
        dispatch('albumselect', { id, name, artists, images });
    }

    function handleKeyDown(event) {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            handleClick();
        }
    }
</script>

<div
    class="album"
    on:click={handleClick}
    on:keydown={handleKeyDown}
    role="button"
    tabindex="0"
    aria-label="Find albums similar to {name} by {artistName}"
>
    {#if coverUrl}
        <img class="album-cover" src={coverUrl} alt="{name} by {artistName}" loading="lazy" />
    {:else}
        <div class="album-cover placeholder">
            <span>No Image</span>
        </div>
    {/if}

    <div class="album-info">
        <p class="album-name">{name}</p>
        <p class="album-artist">{artistName}</p>
    </div>
</div>

<style>
    .album {
        position: relative;
        cursor: pointer;
        border-radius: 8px;
        overflow: hidden;
        transition: transform 0.2s, box-shadow 0.2s;
        background: #1a1a1a;
    }

    .album:hover {
        transform: scale(1.03);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
    }

    .album:focus {
        outline: 2px solid #646cff;
        outline-offset: 2px;
    }

    .album-cover {
        width: 100%;
        aspect-ratio: 1;
        object-fit: cover;
        display: block;
    }

    .album-cover.placeholder {
        display: flex;
        align-items: center;
        justify-content: center;
        background: #333;
        color: #666;
        font-size: 0.8rem;
    }

    .album-info {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 12px 10px;
        background: linear-gradient(transparent, rgba(0, 0, 0, 0.9));
        opacity: 0;
        transition: opacity 0.2s;
    }

    .album:hover .album-info {
        opacity: 1;
    }

    .album-name {
        margin: 0 0 4px 0;
        font-size: 0.85rem;
        font-weight: 600;
        color: white;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .album-artist {
        margin: 0;
        font-size: 0.75rem;
        color: #aaa;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>
