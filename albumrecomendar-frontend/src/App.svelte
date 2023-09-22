<script>
    import Button_Component from "./lib/Button.svelte";
    import Album from './Album.svelte';
    import SimilarAlbums from './SimilarAlbums.svelte';
    import { onMount } from "svelte";

    let albums = []; 
    let similarAlbums = null;

    onMount(async() => {
        const response = await fetch('http://localhost:8080/recommendations?seedArtist=2ANVost0y2y52ema1E9xAZ&seedGenres=hip-hop&seedTracks=0c6xIDDpzE81m2q797ordA&targetEnergy=0.7&targetDanceability=0.7&targetValence=0.1');
        albums = await response.json();
        // console.log(response)
        console.log("on mount display " + albums)
    });

    const handleAlbumClick = async event => {
        const album = event;
        console.log("album clicked info " + album)
        console.log("album clicked name " + album.name)
        const response = await fetch(`http://localhost:8080/search?query=${album.name}`)
        albums = await response.json();
        console.log("album search" + albums)
        // similarAlbums = albums || [];
          
    }
    
    const handleKeyDown = async (event) => {
          if (event.key === 'Enter'){
            // await handleInteraction();
          }
      }
</script>

<div class="container">
    {#if albums.length}
        {#each albums as album}
            <Album {...album} on:albumselect={handleAlbumClick} on:keydown="{handleKeyDown}" />
        {/each}
    
        {#if similarAlbums}        
            <SimilarAlbums {similarAlbums} />
        {/if}

        {:else}
            <p>Loading album ...</p>
    {/if}
</div>

<!-- not working yet 
Continue where we left off 
https://chat.openai.com/c/de07f974-0fbd-420c-80e5-ba5e894317c0
-->



<!-- <Button_Component>Button Text test</Button_Component> -->


<style>

</style>