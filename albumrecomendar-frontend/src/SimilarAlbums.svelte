<script>
    import Album from './Album.svelte';
    export let similarAlbums = [];
    console.log("In SimilarAlbums.svelte")
</script>

    {#if similarAlbums && similarAlbums.length > 0}
        <h2> is it working </h2>
        
        <div class ="similarAlbums-container">
            
            {#each similarAlbums as album (album.id)}
                <Album {...album} />
            {/each}

        </div>
    {:else}
        <p>Loading similar albums ...</p>
    {/if}


<!-- not working yet 
Continue where we left off 
https://chat.openai.com/c/de07f974-0fbd-420c-80e5-ba5e894317c0

-->