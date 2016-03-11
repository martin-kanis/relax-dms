// Initialize the editor
var editor = new JSONEditor(document.getElementById('editor_holder'),{
  // Enable fetching schemas via ajax
  ajax: true,
  
  theme: 'bootstrap2',
  
  iconlib: 'fontawesome4',
  
  disable_properties: true,
  
  disable_edit_json: true,
  
  disable_collapse: true,

  // The schema for the editor
  schema: ${schema}

  // Seed the form with a starting value
  //startval: ${starting_value}
});

// Hook up the submit button to log to the console
document.getElementById('submit').addEventListener('click',function() {
  // Get the value from the editor
  console.log(JSON.stringify(${schema}));
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
});

