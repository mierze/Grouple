'use strict'
function UserRowDirective($state, InviteResponder) {
  return {
    restrict: 'E',
    templateUrl: 'module/list/user/part/user-row.html',
    controller: userRowCtrl,
    controllerAs: 'userRowCtrl'
  };
  
  function userRowCtrl() {
    var vm = this;
    vm.profile = profile;
    vm.decision = decision;
    
    //functions
    function profile(email)
    {
      $state.go('user-profile', {email: email});
    } //end profile
    function decision(post, decision) {
      vm.type = decision + '_friend';
      InviteResponder.respond(post, vm.type, function(data) {                      
        alert(data['message']);
      });
    } //end decision
  }
} //end user row directive

module.exports = UserRowDirective;