'use strict';
module.exports = angular.module('adder', [
    require('./friend-invite').name,
    require('./group-create').name,
    require('./event-create').name
]);