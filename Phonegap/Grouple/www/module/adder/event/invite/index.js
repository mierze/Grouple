'use strict';
module.exports = angular.module('adder.event.invite', [])
.controller('EventInviteController', require('./controller.js'))
.directive('eventInviteRow', require('./part/invite-row.directive.js'));