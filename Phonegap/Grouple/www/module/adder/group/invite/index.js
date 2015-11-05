'use strict';
module.exports = angular.module('adder.group.invite', [])
.controller('GroupInviteController', require('./controller.js'))
.directive('groupInviteRow', require('./part/invite-row.directive.js'));