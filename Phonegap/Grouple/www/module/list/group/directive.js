'use strict'
module.exports = function($state, InviteResponder)
{
  return {
    restrict: 'E',
    templateUrl: 'module/list/group/group-row.html',
    controller: function()
    {
      this.profile = function(id)
      {
        $state.go('group-profile', {id: id});
      };
      this.decision = function(post, decision)
      { //start decision
        this.type = decision + '_group';
        InviteResponder.respond(post, this.type, function(data)
        {                      
          alert(data['message']);
        });
      }; //end decision
    },
    controllerAs: 'group'
  };
}; //end group row directive