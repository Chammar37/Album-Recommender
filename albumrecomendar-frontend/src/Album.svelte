<script>
    export let title;
    export let artist;
    export let coverImageUrl;

    console.log(title +", "+artist + ", "+ coverImageUrl)
  </script>
  
  <div class="album">
    <img src="{coverImageUrl}" alt="{title} by {artist}" />
    <h2>{title}</h2>
    <h3>{artist}</h3>
  </div>
  
  <style>
    .album {
      /* Add your album component styling here */
    }
  </style>
  
  