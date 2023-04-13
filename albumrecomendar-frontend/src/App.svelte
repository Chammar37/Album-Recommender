<script>
    import Button_Component from "./lib/Button.svelte";
    import Album from './Album.svelte';
    import { onMount } from "svelte";

    let album; 

    onMount(async() => {
        const response = await fetch('http://localhost:8080/api/album?query=Madvillainy');
        album = await response.json();
        console.log(response)
    });

    
</script>


{#if album}
    <Album {...album} />
{:else}
    <p>Loading album ...</p>
{/if}


<Button_Component>Button Text test</Button_Component>


<style>

</style>