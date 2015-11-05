'use strict'
module.exports = function($http, ProfileEditer)
{
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/group/part/group-edit.html',
    controller: function()
    {
      alert('include editer here');
    },
    controllerAs: 'groupEditCtrl'
  };
}; //end edit group profile directive