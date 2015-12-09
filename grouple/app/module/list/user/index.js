'use strict';
module.exports = angular.module('list.user', [])
.controller('UserListController', require('./controller.js'))
.directive('userRow', require('./part/user-row.directive.js'));