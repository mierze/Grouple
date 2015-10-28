'use strict';
module.exports = angular.module('adder.group.invite', [])
.controller('GroupInviteController', require('./controller.js'))
.directive('groupInviteRow', require('./directive.js'));