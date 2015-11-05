'use strict';
module.exports = angular.module('message.entity', [])
.controller('EntityMessageController', require('./controller.js'))
.directive('messageRow', require('./part/message-row.directive.js'));