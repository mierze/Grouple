'use strict';
module.exports = angular.module('list.badge', [])
  .controller('BadgeListController', require('./controller'))
  .directive('badgeItem', require('./part/badge-item.directive'));