'use strict';
module.exports = angular.module('service.profile.user', [])
  .factory('UserProfileGetter', require('./profile'))
  .factory('UserImageGetter', require('./image'))
  .factory('UserEditer', require('./edit'));