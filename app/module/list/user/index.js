'use strict';
module.exports = angular.module('list.user', [])
  .controller('UserListController', require('./controller'))
  .directive('userRow', require('./part/user-row.directive'));