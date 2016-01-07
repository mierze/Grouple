'use strict';
module.exports = angular.module('list.event', [])
  .controller('EventListController', require('./controller.js'))
  .directive('eventRow', require('./part/event-row.directive.js'));