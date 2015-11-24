'use strict';
module.exports = angular.module('list.badge', [])
.directive('badgeItem', require('./part/badge-item.directive.js'));