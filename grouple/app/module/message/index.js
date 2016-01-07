'use strict';
module.exports = angular.module('message', [
  require('./contact').name,
  require('./user').name,
  require('./entity').name
]);