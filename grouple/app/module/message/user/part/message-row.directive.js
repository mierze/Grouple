'use strict'
module.exports = function($state)
{
  return {
    restrict: 'E',
    templateUrl: 'module/message/user/part/message-row.html',
    controller: function()
    {
      var vm = this; 
      var storage = window.localStorage;
      this.profile = profile;
      this.isUser = isUser;
      
      //functions
      function profile(email)
      {
        $state.go('user-profile', {id: email});
      };
      function isUser(from)
      {
        if (from === storage.getItem('email')) {
          return true;
        }
        return false;
      };
    },
    controllerAs: 'userMessageRowCtrl'
  };
}; //end message row directive
