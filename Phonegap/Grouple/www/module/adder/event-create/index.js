'use strict';
module.exports = angular.module('adder.event', [])
.controller('EventCreateController', require('./controller.js'))
.directive('groupInvite', require('./directive.js'));