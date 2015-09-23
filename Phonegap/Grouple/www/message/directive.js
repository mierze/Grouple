(function() {
  //create module directives
  var storage = window.localStorage; //grab local storage
 
  angular.module('directive')
  //message row directive
  .directive("messageRow", function() {
    return {
      restrict: 'E',
      templateUrl: "message/partial/message-row.html",
      controller: function()
      {
        //PANDA change to id
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
      templateUrl: "message/partial/contact-row.html",
      controller: function()
      {
        this.startMessages = function(contact)
        {
          alert("this is " +JSON.stringify(contact));
          var id = (contact.sender === storage.getItem("email")) ? contact.receiver : contact.sender;
          $state.go('messages', {id: id});
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