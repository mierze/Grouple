'use strict';
module.exports = angular.module('message.message', [])
.controller('MessageController', require('./controller.js'))
.directive('messageRow', require('./directive.js'));