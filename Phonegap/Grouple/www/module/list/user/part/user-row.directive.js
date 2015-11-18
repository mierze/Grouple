'use strict'
module.exports = function($state, InviteResponder)
{
  return {
    restrict: 'E',
    templateUrl: 'module/list/user/part/user-row.html',
    controller: function()
    {
      var vm = this;
      vm.profile = profile;
      vm.decision = decision;
      
      //functions
      function profile(id)
      {
        $state.go('user-profile', {id: id});
      };
      function decision(post, decision)
      { //start decision
        vm.type = decision + '_friend';
        InviteResponder.respond(post, vm.type, function(data)
        {                      
          alert(data['message']);
        });
      }; //end decision
    },
    controllerAs: 'userRowCtrl'
  };
}; //end user row directive