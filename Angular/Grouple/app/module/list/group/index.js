'use strict';
module.exports = angular.module('list.group', [])
.controller('GroupListController', require('./controller.js'))
.directive('groupRow', require('./part/group-row.directive.js'));