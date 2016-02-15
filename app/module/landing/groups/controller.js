'use strict'
function GroupsController($rootScope) {
  $rootScope.$broadcast('setTitle', 'Groups');
}

module.exports = GroupsController;