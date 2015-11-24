'use strict';
module.exports = angular.module('list.event', [])
.directive('eventRow', require('./part/event-row.directive.js'));