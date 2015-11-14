'use strict'
module.exports = function()
{ //group invite row directive
    return {
      restrict: 'E',
      templateUrl: "module/adder/group/invite/part/invite-row.html",
      controller: function()
      { //start friend invite list controller
        this.post = {};
      }, //end friend invite list controller
      controllerAs: "inviteRowCtrl"
    };
}; //end group invite row directive