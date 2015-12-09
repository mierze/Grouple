'use strict';
module.exports = angular.module('profile.event', [])
.controller('EventProfileController', require('./controller'))
.directive('eventEdit', require('./part/event-edit.directive.js'));