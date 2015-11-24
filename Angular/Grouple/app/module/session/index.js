'use strict';
module.exports = angular.module('session', [
    require('./register').name,
    require('./login').name//,
   // require('./settings')
]);