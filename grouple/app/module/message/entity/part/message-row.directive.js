'use strict'
function EntityMessageRowDirective($state) {
  return {
    restrict: 'E',
    templateUrl: 'module/message/entity/part/message-row.html',
    controller: entityMessageRowCtrl,
    controllerAs: 'entityMessageRowCtrl'
  };
  
  function entityMessageRowCtrl() {
    var vm = this;
    vm.profile = profile;

    //functions
    function profile(id) {
      $state.go('user-profile', {id: id});
    }
  } //end entity message row controller
} //end entity message row directive

module.exports = EntityMessageRowDirective;
