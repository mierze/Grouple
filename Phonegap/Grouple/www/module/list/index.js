'use strict';
module.exports = angular.module('list', [
    require('./user').name,
    require('./group').name,
    require('./event').name,
    require('./badge').name
])
.controller('ListController', require('./controller.js'));