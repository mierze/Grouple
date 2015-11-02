'use strict';
module.exports = angular.module('profile.event', [])
.directive('eventEdit', require('./directive.js'))
.controller('EventProfileController', require('./controller'));