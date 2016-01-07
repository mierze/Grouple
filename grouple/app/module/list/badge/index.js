'use strict';
module.exports = angular.module('list.badge', [])
  .controller('BadgeListController', require('./controller.js'))
  .directive('badgeItem', require('./part/badge-item.directive.js'));