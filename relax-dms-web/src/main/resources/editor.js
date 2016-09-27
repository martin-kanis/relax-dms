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
});

// Hook up the submit button to log to the console
document.getElementById('submit').addEventListener('click',function() {
  // Get the value from the editor
  console.log(send(JSON.stringify(editor.getValue())));
  
});

// clear button
document.getElementById('clear').addEventListener('click',function() {
  var values = editor.getValue();
  for (value in values) {
      if (value != "author") {
          editor.getEditor("root." + value).setValue("");
      }
  }
});

// Hook up the validation indicator to update its 
// status whenever the editor changes
editor.on('change',function() {
  // Get an array of errors from the validator
  var errors = editor.validate();
  console.log(errors);
  
  // set default author based on the logged user
  var author = editor.getEditor('root.author');
  author.setValue("${author}");
  
  if (author != "")
    editor.getEditor('root.author').disable();
});

