'use strict'
function GroupsController($rootScope) {
  //groups controller
  var vm = this;
  $rootScope.$broadcast('setTitle', 'Groups');
}; //end groups controller

module.exports = GroupsController;