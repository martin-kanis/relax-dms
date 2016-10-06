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
    if (send(JSON.stringify(editor.getValue()))) {
        console.log("Ok");
        clear();
        feedback(true);
    } else {
        clear();
        feedback(false);
    }
  
});

// clear button
document.getElementById('clear').addEventListener('click',function() {
    clear();
});

// Hook up the validation indicator to update its 
// status whenever the editor changes
editor.on('change',function() {
  // Get an array of errors from the validator
  var errors = editor.validate();
  console.log("My err: " + errors);
  
  // set default author based on the logged user
  var author = editor.getEditor('root.author');
  author.setValue("${author}");
  
  if (author != "")
    editor.getEditor('root.author').disable();
});

function clear() {
    var values = editor.getValue();
    for (value in values) {
        if (value != "author") {
            editor.getEditor("root." + value).setValue("");
        }
    }
    
    document.getElementById('feedback1').classList.add("hide");
    document.getElementById('feedback2').classList.add("hide");
}

function feedback(success) {
    if (success)
        document.getElementById('feedback1').classList.remove("hide");
    else 
        document.getElementById('feedback2').classList.remove("hide");
}

