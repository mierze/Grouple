(function() {
  //create module directives
  var storage = window.localStorage; //grab local storage
 
  angular.module('directive')
  //message row directive
  .directive("messageRow", function() {
    return {
      restrict: 'E',
      templateUrl: "template/message-row.html",
      controller: function()
      {
        this.profile = function(email)
        {
          document.location.href="user-profile.html?email="+email;
        };
      },
      controllerAs: "msgCtrl"
    };
  }) //end message row directive
  
  //contact row directive
  .directive("contactRow", function() {
    return {
      restrict: 'E',
      templateUrl: "template/contact-row.html",
      controller: function()
      {
        this.startMessages = function(contact)
        {
          var email = (contact.sender === storage.getItem("email")) ? contact.receiver : contact.sender;
          document.location.href = "messages.html?email=" + email;
        };
        this.imgEnc = function(image)
        {
          return "data:image/png;base64," + image;
        }
      },
      controllerAs: "contactCtrl"
    };
  }); //end contact row directive
})();