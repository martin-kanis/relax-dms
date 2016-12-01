// Initialize the editor
var editor = new JSONEditor(document.getElementById('editor_holder'),{
  // Enable fetching schemas via ajax
  ajax: true,
  
  theme: 'bootstrap2',
  
  iconlib: 'fontawesome4',
  
  disable_properties: true,
  
  disable_edit_json: true,
  
  disable_collapse: true,
  
  show_errors: 'always',

  // The schema for the editor
  schema: ${schema},
  
  startval: ${startval}
});

var errors = [];
var conflict = false;

if ("${usecase}" === "UPDATE") {
    editor.disable(); 
}

var dataString = '${diffData}';
var data = JSON.parse(dataString);
if (Object.keys(data).length > 0) {
    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            var p = editor.getEditor(key);
            p.enable();

            window.errors.push({
                path: key,
                property: 'format',
                message: 'New value: ' + data[key]
            });     
        }
    }
    window.conflict = true;
    editor.root.showValidationErrors(window.errors);
}

var saveButton = document.getElementById('save');
var clearButton = document.getElementById('clear');
var editButton = document.getElementById('edit');
var cancelButton = document.getElementById('cancel');

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
if (clearButton) {
    clearButton.addEventListener('click',function() {
        clear();
    });
}

// edit button
if (editButton) {
    if (conflict) {
        saveButton.style.cssText = "display:inline;"
        editButton.style.cssText = "display:none;"
        cancelButton.style.cssText = "display:inline;"
    }
    editButton.addEventListener('click',function() {
        editor.enable();
        editor.getEditor('root.author').disable();
        
        saveButton.style.cssText = "display:inline;"
        editButton.style.cssText = "display:none;"
        cancelButton.style.cssText = "display:inline;"
    });
}

// cancel button
if (cancelButton) {
    cancelButton.addEventListener('click',function() {
        editor.disable();
        editor.root.showValidationErrors([]);
        editor.validate();
        
        saveButton.style.cssText = "display:none;"
        editButton.style.cssText = "display:inline;"
        cancelButton.style.cssText = "display:none;"
    });
}

// Hook up the validation indicator to update its 
// status whenever the editor changes
editor.on('change',function() {
    // Get an array of errors from the validator    
    editor.root.showValidationErrors(window.errors);
    window.errors = editor.validate();
    
    // set default author based on the logged user
    var author = editor.getEditor('root.author');

    if (author.getValue() == "")
      author.setValue('${author}');
    else
      author.disable();
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