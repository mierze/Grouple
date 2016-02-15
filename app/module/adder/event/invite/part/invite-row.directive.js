'use strict'
function EventInviteRowDirective($state) {
  return {
    restrict: 'E',
    templateUrl: 'module/adder/event/invite/part/invite-row.html',
    controller: eventInviteRowCtrl,
    controllerAs: 'eventInviteRowCtrl'
  };
  
  function eventInviteRowCtrl() {
    var vm = this;
    vm.checked = false;
    vm.toggleRow = function() {
      if (vm.checked)
        vm.checked = false;
      else
        vm.checked = true;
    } //end toggle row
  } //end event invite row controller
} //end event invite row directive

module.exports = EventInviteRowDirective;
