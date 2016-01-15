'use strict'
function groupInviteRowDirective() { //group invite row directive
    return {
      restrict: 'E',
      templateUrl: "module/adder/group/invite/part/invite-row.html",
      controller: groupInviteRowCtrl, 
      controllerAs: "groupInviteRowCtrl"
    };
    
    function groupInviteRowCtrl() {
        var vm = this;
        vm.role = '-';
        vm.toggleRole = toggleRole;
        
        //functions
        function toggleRole() {
            if (vm.role === 'M') {
              vm.role = 'A'
            }
            else if (vm.role === '-') {
              vm.role = 'M';
            }
            else if (vm.role === 'A') {
              vm.role = '-';
            }
            alert("sub toggle now is " + vm.role);
        } //end toggle role
    } //end group invite row controller
} //end group invite row directive

module.exports = groupInviteRowDirective;