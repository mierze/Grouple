'use strict';
module.exports = angular.module('profile.user', [])
.directive('userEdit', require('./directive.js'))
.controller('UserProfileController', require('./controller'));