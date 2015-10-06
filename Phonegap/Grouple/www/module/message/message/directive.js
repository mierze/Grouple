'use strict'
module.exports = function($state)
{
  return {
    restrict: 'E',
    templateUrl: "module/message/message/partial/message-row.html",
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
}; //end message row directive
