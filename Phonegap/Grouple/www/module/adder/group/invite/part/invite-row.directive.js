'use strict'
module.exports = function()
{ //group invite row directive
    return {
      restrict: 'E',
      templateUrl: "module/adder/group/invite/part/invite-row.html",
      controller: function()
      { //start friend invite list controller
        //TODO code role here
        var vm = this;
        vm.role = '-';
        vm.toggleRole = function()
        {
            if (vm.role === 'M')
            {
              vm.role = 'A'
            }
            else if (vm.role === '-') {
              vm.role = 'M';
            }
            else if (vm.role === 'A')
            {
              vm.role = '-';
            }
            alert("sub toggle now is " + vm.role);
        };
      }, //end friend invite list controller
      controllerAs: "inviteRowCtrl"
    };
}; //end group invite row directive