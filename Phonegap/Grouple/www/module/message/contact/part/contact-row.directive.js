'use strict'
module.exports = function($state)
{ //contact row directive
return {
    restrict: 'E',
    templateUrl: 'module/message/contact/part/contact-row.html',
    controller: function()
    {
      this.imgEnc = function(image)
      {
        return 'data:image/png;base64,' + image;
      }
    },
    controllerAs: 'contactRowCtrl'
  };
}; //end contact row directive