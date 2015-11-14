'use strict'
module.exports = function($state, InviteResponder)
{ //event row directive
  return {
    restrict: 'E',
    templateUrl: 'module/list/event/part/event-row.html',
    controller: function()
    {
      this.profile = function(id)
      {
        $state.go('event-profile', {id: id});
      };
      this.decision = function(id, decision)
      { //start decision
        var post = {};
        post.id = id;
        post.user = storage.getItem('email');
        InviteResponder.respond(post, decision, /* content of response */'event', function(data)
        {                      
          alert(data['message']);
          if (data['success'] === 1)
          {
            $state.go($state.current, {content: 'event_invites', id: storage.getItem('email')}, {reload: true});
          }
        });
      }; //end decision
    },
    controllerAs: 'eventRowCtrl'
  };
}; //end event row directive
