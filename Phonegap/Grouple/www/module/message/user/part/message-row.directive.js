'use strict'
module.exports = function($state)
{
  return {
    restrict: 'E',
    templateUrl: 'module/message/user/part/message-row.html',
    controller: function()
    {
      
      var storage = window.localStorage;
      this.isUser = function(from)
      {
        if (from === storage.getItem('email')) {
          return true;
        }
        return false;
      };
      this.profile = function(email)
      {
        $state.go('user-profile', {id: email});
      };
    },
    controllerAs: 'userMessageRowCtrl'
  };
}; //end message row directive
