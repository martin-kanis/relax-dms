(function my_show(doc, req) {
    return '<h1>' + doc.name + '</h1>' + '<h3>Author: </h3>' + doc.author  + '<p>' + doc.phoneNumber + '</p>';
})