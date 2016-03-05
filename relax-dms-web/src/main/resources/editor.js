// Initialize the editor
var editor = new JSONEditor(document.getElementById('editor_holder'),{
  // Enable fetching schemas via ajax
  ajax: true,

  // The schema for the editor
  schema: ${schema}

  // Seed the form with a starting value
  //startval: ${starting_value}
});

// Hook up the submit button to log to the console
document.getElementById('submit').addEventListener('click',function() {
  // Get the value from the editor
  console.log(JSON.stringify(editor.getValue()));
  send(JSON.stringify(editor.getValue()));
});

// clear button
document.getElementById('clear').addEventListener('click',function() {
  // TODO clear
  //editor.setValue(${starting_value});
});

// Hook up the validation indicator to update its 
// status whenever the editor changes
editor.on('change',function() {
  // Get an array of errors from the validator
  var errors = editor.validate();

  var indicator = document.getElementById('valid_indicator');

  // Not valid
  if(errors.length) {
    indicator.className = 'label alert';
    indicator.textContent = 'not valid';
  }
  // Valid
  else {
    indicator.className = 'label success';
    indicator.textContent = 'valid';
  }
});

