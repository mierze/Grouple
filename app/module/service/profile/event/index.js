'use strict';
module.exports = angular.module('service.profile.event', [])
  .factory('EventProfileGetter', require('./profile'))
  .factory('EventImageGetter', require('./image'))
  .factory('EventEditer', require('./edit'));
