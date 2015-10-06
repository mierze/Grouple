'use strict';
module.exports = angular.module('adder.group', [])
.controller('GroupCreateController', require('./controller.js'))
.directive('friendInvite', require('./directive.js'));