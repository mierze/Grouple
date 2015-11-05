'use strict';
module.exports = angular.module('message.user', [])
.controller('UserMessageController', require('./controller.js'))
.directive('messageRow', require('./part/message-row.directive.js'));