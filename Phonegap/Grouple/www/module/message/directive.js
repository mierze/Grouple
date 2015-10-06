module.exports = function()
{ //create module directives
  angular.module('message')
  //message row directive
  .directive("messageRow", function($state) {
    return {
      restrict: 'E',
      templateUrl: "module/message/layout/partial/message-row.html",
      controller: function()
      {
        //PANDA change to id
        var float = "100%";
        this.profile = function(email)
        {
          $state.go('user-profile', {id: email});
        };
      },
      controllerAs: "msgCtrl"
    };
  }) //end message row directive
  
  //contact row directive
  .directive("contactRow", function($state) {
    return {
      restrict: 'E',
      templateUrl: "module/message/layout/partial/contact-row.html",
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
  }); //end contact row directive
};