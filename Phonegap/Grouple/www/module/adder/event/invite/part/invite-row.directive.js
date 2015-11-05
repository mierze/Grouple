'use strict'
module.exports = function($state)
{ //event invite row directive
  return {
    restrict: 'E',
    templateUrl: 'module/adder/event/invite/part/invite-row.html',
    controller: function()
    {
      //PANDA change to id
      this.toggleRole = function()
      {
      };
    },
    controllerAs: 'inviteRowCtrl'
  };
}; //end event invite row directive
