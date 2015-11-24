'use strict'
module.exports = function($state, InviteResponder)
{
  return {
    restrict: 'E',
    templateUrl: 'module/list/group/part/group-row.html',
    controller: function()
    {
      var vm = this;
      var storage = window.localStorage;
      vm.profile = profile;
      vm.decision = decision;
      
      //functions
      function profile(id)
      {
        $state.go('group-profile', {id: id});
      };
      function decision(id, decision)
      { //start decision;
        var post = {};
        post.id = id;
        post.user = storage.getItem('email');
        InviteResponder.respond(post, decision, /* content of response */'group', function(data)
        {                      
          alert(data['message']);
          if (data['success'] === 1)
          {
            $state.go($state.current, {content: 'group_invites', id: storage.getItem('email')}, {reload: true});
          }
        });
      }; //end decision
    },
    controllerAs: 'groupRowCtrl'
  };
}; //end group row directive