'use strict';
module.exports = angular.module('adder', [
    require('./group').name,
    require('./event').name,
    require('./friend').name
    //require('./user').name
]);