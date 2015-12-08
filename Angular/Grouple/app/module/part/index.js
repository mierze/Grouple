'use strict';
module.exports = angular.module('part', [])
.directive('actionBar', require('./action-bar/directive.js'))
.directive('sadGuy', require('./sad-guy/directive.js'));