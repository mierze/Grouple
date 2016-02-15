'use strict';
module.exports = angular.module('service.message', [
    require('./user').name,
    require('./entity').name
  ]);
