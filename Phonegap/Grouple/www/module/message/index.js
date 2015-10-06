'use strict';
module.exports = angular.module('message', [
    require('./contact').name,
    require('./message').name
]);