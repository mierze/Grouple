'use strict';
module.exports = angular.module('service.list.event', [])
  .factory('EventListGetter', require('./get'));
