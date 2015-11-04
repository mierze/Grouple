'use strict'
module.exports = function($state)
{ //entity message row directive
  return {
    restrict: 'E',
    templateUrl: 'module/message/entity/message-row.html',
    controller: function()
    {
      //PANDA change to id
      this.profile = function(email)
      {
        $state.go('user-profile', {id: email});
      };
    },
    controllerAs: 'entityMessageCtrl'
  };
}; //end entity message row directive
