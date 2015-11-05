'use strict'
module.exports = function($state, InviteResponder)
{
  return {
    restrict: 'E',
    templateUrl: 'module/list/user/part/user-row.html',
    controller: function()
    {
      this.profile = function(id)
      {
        $state.go('user-profile', {id: id});
      };
      this.decision = function(post, decision)
      { //start decision
        this.type = decision + '_friend';
        InviteResponder.respond(post, this.type, function(data)
        {                      
          alert(data['message']);
        });
      }; //end decision
    },
    controllerAs: 'userRowCtrl'
  };
}; //end user row directive