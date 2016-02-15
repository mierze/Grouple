'use strict'
module.exports = angular.module('component.action-bar', [])
  .directive('actionBar', require('./directive'))
  .directive('navMenu', require('./part/nav-menu.directive'));