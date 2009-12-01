TK={}

TK.showFlashMessage = function(message) {
    var flashMessage = Ext.get('flash_message');
    var innerHtml = "<div class='flash_message_content'>";
    innerHtml += message;
    innerHtml += "</div>";

    flashMessage.update(innerHtml);
}
