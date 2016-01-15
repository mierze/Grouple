'use strict'
function UserMessageRowDirective($state) {
  return {
    restrict: 'E',
    templateUrl: 'module/message/user/part/message-row.html',
    controller: userMessageRowCtrl,
    controllerAs: 'userMessageRowCtrl'
  };
  
  function userMessageRowCtrl() {
    var vm = this; 
    var storage = window.localStorage;
    vm.profile = profile;
    vm.isUser = isUser;
    
    //functions
    function profile(email) {
      $state.go('user-profile', {id: email});
    }
    function isUser(from) {
      if (from === storage.getItem('email')) {
        return true;
      }
      return false;
    }
  } //end user message row controller
} //end user message row directive

module.exports = UserMessageRowDirective;