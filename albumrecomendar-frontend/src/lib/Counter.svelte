<!--
  
  Will not work by just calling string in the markup
  let string = `You have clicked ${count} times.`

  Inside of this reactive statement ($: declares reactive statement), any value we depend on 
  Will make this statement run again if the dependent variable changes.
  
  REACTIVE STATEMENTS will run when we first compile the script, and if any variable(s) the reactive
  stateament depends on has some change.
  
  In the markup, the variables you are receiving from <script> will be updated, though the 
  <script> section only runs once. 
  
  reactive statement will only work if variable we are monitoring is in the statement itself
  //i.e, having a method name in the reactive statement will not update variables we are trying to target
  which are in the method
  
  /*
  function getCount(){
    return count;
  }
  $: string = `You have clicked ${getCount()} times.` This will not give you an updated variable count.
  */

-->

<script>
  export let initialCount;
  export let maxCount;
  let count = initialCount;
  let string;  

  console.log($$props, $$restProps)

  $:if(count >5){ 
    console.log(`The count is ${count}`)
  }

  function increment(){
    if(count == maxCount) return;
      count += 1;
  }


  function getTotal(x){
    return x;
  }

  //this also works for 1 variable
  $: string = `You have clicked ${getTotal(count)} times.`

</script>

{string}
<button on:click={increment}> 
  Clicked {count} {count == 1 ? 'time' : 'times'}</button>
  


  <h3>{string}</h3>
  <!-- <h3>You have clicked {count} times.</h3> -->

<style>
  
  button {
    padding: 15px 20px;
  }
  
</style>