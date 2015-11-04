'use strict'
module.exports = function($state)
{ //event invite row directive
  return {
    restrict: 'E',
    templateUrl: 'module/adder/event/invite/event-invite-row.html',
    controller: function()
    {
      //PANDA change to id
      this.toggleRole = function()
      {
      };
    },
    controllerAs: 'eventInviteRowCtrl'
  };
}; //end event invite row directive
