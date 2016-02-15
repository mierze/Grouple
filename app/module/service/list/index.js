'use strict';
module.exports = angular.module('service.list', [
  require('./user').name,
  require('./group').name,
  require('./event').name,
  require('./badge').name
]);
