'use strict'
function MessageRowDirective($state) {
  return {
    //message row directive
    restrict: 'E',
    templateUrl: 'module/message/user/part/message-row.html',
    controller: function() {
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
    },
    controllerAs: 'userMessageRowCtrl'
  };
} //end message row directive

module.exports = MessageRowDirective;