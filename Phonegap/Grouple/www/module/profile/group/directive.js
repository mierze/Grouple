'use strict'
module.exports = function($http, ProfileEditer)
{
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/group/group-edit.html',
    controller: function()
    {
      alert('include editer here');
    },
    controllerAs: 'groupEdit'
  };
}; //end edit group profile directive