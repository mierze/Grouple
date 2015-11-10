'use strict'
module.exports = function($state)
{ //entity message row directive
  return {
    restrict: 'E',
    templateUrl: 'module/message/entity/part/message-row.html',
    controller: function()
    {
      //PANDA change to id
      this.profile = function(email)
      {
        $state.go('user-profile', {id: email});
      };
    },
    controllerAs: 'entityMessageRowCtrl'
  };
}; //end entity message row directive
