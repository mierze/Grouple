'use strict';
module.exports = angular.module('service.list.badge', [])
  .factory('BadgeGetter', require('./badges'));
