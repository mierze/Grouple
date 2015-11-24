'use strict';
module.exports = angular.module('list.user', [])
    .directive('userRow', require('./part/user-row.directive.js'));