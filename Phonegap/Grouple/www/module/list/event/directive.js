'use strict'
module.exports = function($state, InviteResponder)
{ //event row directive
  return {
    restrict: 'E',
    templateUrl: 'module/list/event/event-row.html',
    controller: function()
    {
      this.profile = function(id)
      {
        $state.go('event-profile', {id: id});
      };
      this.decision = function(post, decision)
      { //start decision
        this.type = decision + '_event';
        InviteResponder.respond(post, this.type, function(data)
        {                      
          alert(data['message']);
        });
      }; //end decision
    },
    controllerAs: 'event'
  };
}; //end event row directive
