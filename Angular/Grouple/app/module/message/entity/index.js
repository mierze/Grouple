'use strict';
module.exports = angular.module('message.entity', [])
.controller('EntityMessageController', require('./controller.js'))
.directive('entityMessageRow', require('./part/message-row.directive.js'));