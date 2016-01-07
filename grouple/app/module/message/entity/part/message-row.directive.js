'use strict'
function EntityMessageRowDirective($state) {
  return {
    restrict: 'E',
    templateUrl: 'module/message/entity/part/message-row.html',
    controller: function() {
      vm.profile = profile;

      //functions
      function profile(id) {
        $state.go('user-profile', {id: id});
      }
    },
    controllerAs: 'entityMessageRowCtrl'
  };
} //end entity message row directive

module.exports = EntityMessageRowDirective;
