'use strict';
module.exports = angular.module('service.profile.group', [])
  .factory('GroupProfileGetter', require('./profile'))
  .factory('GroupImageGetter', require('./image'))
  .factory('GroupEditer', require('./edit'));
