'use strict';
module.exports = angular.module('profile', [
    require('./user').name,
    require('./group').name,
    require('./event').name
]);