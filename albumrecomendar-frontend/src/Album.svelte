<script>
    import {createEventDispatcher } from 'svelte';
    
    export let id;
    export let name;
    export let artist;
    export let images;
    
    // create an event dispatcher
    const dispatch = createEventDispatcher();

    // const handleInteraction = async () => {
      
      // try {
          const handleClick = async () => {
            console.log("button clicked")
            const response = await fetch(`http://localhost:8080/search?query=${name}`)
            if (!response.ok){
              throw new Error('HTTP error! Status: ${response.status}');
            }
            const data = await response.json();
            console.log(data);

            // dispatch a custom event with the selected album's data
            dispatch('albumselect', { id, name, artist, images});
            
          }
      // } catch (error) { 
          // console.error('An error occurred and is being handled: ${error}')
      // }
    // }

    const handleKeyDown = async (event) => {
          if (event.key === 'Enter'){
            // await handleInteraction();
          }
      }

    // console.log(name + ", " + artist[0].name + ", " + cover)
  </script>
  
  <!-- BELOW DIV WORKS -->
  <div class="album" on:click={handleClick} on:keydown="{handleKeyDown}"> 
    <!-- <div class="album" > -->
    <img class="album-cover" src="{images[0].url}" alt="{name} by {artist[0].name}" />
    <!-- <h2>{name}</h2> -->
    <!-- <h3>{artist[0].name}</h3> -->
  </div>
  
  <style>
    .album {
      cursor: pointer;
    }
  </style>
  
  