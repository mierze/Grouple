'use strict'
module.exports = function($state)
{
  return {
    restrict: 'E',
    templateUrl: "module/message/user/message-row.html",
    controller: function()
    {
      //PANDA change to id
      var float = "100%";
      this.profile = function(email)
      {
        $state.go('user-profile', {id: email});
      };
    },
    controllerAs: "userMessageCtrl"
  };
}; //end message row directive
