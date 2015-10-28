'use strict'
module.exports = function()
{ //group invite row directive
    return {
      restrict: 'E',
      templateUrl: "module/adder/group/invite/group-invite-row.html",
      controller: function()
      { //start friend invite list controller
        this.post = {};
        this.toggleRole = function()
        {
            alert("toggleRole");  
        };
      }, //end friend invite list controller
      controllerAs: "groupInviteRowCtrl"
    };
}; //end group invite row directive