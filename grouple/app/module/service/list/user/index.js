'use strict';
module.exports = angular.module('service.list.user', [])
  .factory('UserListGetter', require('./get'));
