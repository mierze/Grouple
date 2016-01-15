'use strict';
module.exports = angular.module('root', [
  require('./landing').name,
  require('./adder').name,
  require('./list').name,
  require('./message').name,
  require('./component').name,
  require('./profile').name,
  require('./service').name,
  require('./session').name
  //require('./widget').name
]);