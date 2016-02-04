'use strict';
module.exports = angular.module('profile.user', [])
  .controller('UserProfileController', require('./controller'))
  .directive('userEdit', require('./part/user-edit.directive'));