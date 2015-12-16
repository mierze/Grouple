'use strict'
function InviteRowDirective($state) {
  //event invite row directive
  return {
    restrict: 'E',
    templateUrl: 'module/adder/event/invite/part/invite-row.html',
    controller: function() {
      var vm = this;
      vm.checked = false;
      //PANDA change to id
      vm.toggleRow = function() {
        if (vm.checked)
          vm.checked = false;
        else
          vm.checked = true;
      };
    },
    controllerAs: 'inviteRowCtrl'
  };
}; //end event invite row directive

module.exports = InviteRowDirective;
