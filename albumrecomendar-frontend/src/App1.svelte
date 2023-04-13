<script>

import Counter from './lib/Counter.svelte';

const maxCount =6;

const props_test = {
  maxCount: 10,
  initialCount : 5
};
  // let name = 'Svelte';
  // let src = "http://picsum.com/200/200";
  // let string = 'Some <strong>string</strong> with HTML'
</script>

<Counter {...props_test}, x='y'/>


<!-- <img src={src} alt="Some text {name}" />
<h1> Hello {name} !</h1>
<p> {@html string}</p> -->

<style>

  /* h1{
    color: red;
  } */
</style>