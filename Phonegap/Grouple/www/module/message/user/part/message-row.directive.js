'use strict'
module.exports = function($state)
{
  return {
    restrict: 'E',
    templateUrl: "module/message/user/part/message-row.html",
    controller: function()
    {
      //PANDA change to id
      var float = "100%";
      this.profile = function(email)
      {
        $state.go('user-profile', {id: email});
      };
    },
    controllerAs: "messageRowCtrl"
  };
}; //end message row directive
