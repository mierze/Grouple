'use strict';
module.exports = angular.module('profile.group', [])
.controller('GroupProfileController', require('./controller'))
.directive('groupEdit', require('./part/group-edit.directive.js'));