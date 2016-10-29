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
  schema: ${schema},
  
  startval: ${startval}
});

// Hook up the submit button to log to the console
var saveButton = document.getElementById('save');
if (saveButton) {
    saveButton.addEventListener('click',function() {
      // Get the value from the editor
        if (send(JSON.stringify(editor.getValue()))) {
            feedback(true);
        } else {
            clear();
            feedback(false);
        }

    });
}

// clear button
var clearButton = document.getElementById('clear');
if (clearButton) {
    clearButton.addEventListener('click',function() {
        clear();
    });
}

var editButton = document.getElementById('edit');
if (editButton) {
    editButton.addEventListener('click',function() {
        editor.enable();
        editor.getEditor('root.author').disable();
        
        saveButton.style.cssText = "display:inline;"
        editButton.style.cssText = "display:none;"
        cancelButton.style.cssText = "display:inline;"
    });
}

var cancelButton = document.getElementById('cancel');
if (cancelButton) {
    cancelButton.addEventListener('click',function() {
        editor.disable();
        
        saveButton.style.cssText = "display:none;"
        editButton.style.cssText = "display:inline;"
        cancelButton.style.cssText = "display:none;"
    });
}

// Hook up the validation indicator to update its 
// status whenever the editor changes
editor.on('change',function() {
  // Get an array of errors from the validator
  var errors = editor.validate();
  console.log("My err: " + errors);
  
  // set default author based on the logged user
  var author = editor.getEditor('root.author');

  if (author.getValue() == "")
    author.setValue("${author}");
  else
    editor.getEditor('root.author').disable();
});

if ("${usecase}" === "UPDATE") {
    editor.disable(); 
}

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

