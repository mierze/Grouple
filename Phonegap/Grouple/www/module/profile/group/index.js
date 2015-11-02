'use strict';
module.exports = angular.module('profile.group', [])
.directive('groupEdit', require('./directive.js'))
.controller('GroupProfileController', require('./controller'));