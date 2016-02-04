'use strict';
module.exports = angular.module('list.event', [])
  .controller('EventListController', require('./controller'))
  .directive('eventRow', require('./part/event-row.directive'));