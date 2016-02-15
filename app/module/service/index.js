'use strict';
module.exports = angular.module('service', [
    require('./adder').name,
    require('./list').name,
    require('./message').name,
    require('./profile').name,
    require('./session').name
  ])
  .factory('Getter', require('./get'))
  .factory('Poster', require('./post'));
