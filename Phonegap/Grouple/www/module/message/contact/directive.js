'use strict'
module.exports = function($state)
{ //contact row directive
return {
    restrict: 'E',
    templateUrl: "module/message/contact/contact-row.html",
    controller: function()
    {
      this.startMessages = function(contact)
      {
        $state.go('messages', {id: contact.contact});
      };
      this.imgEnc = function(image)
      {
        return "data:image/png;base64," + image;
      }
    },
    controllerAs: "contactCtrl"
  };
}; //end contact row directive