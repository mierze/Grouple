'use strict';
module.exports = angular.module('message.user', [])
  .controller('UserMessageController', require('./controller.js'))
  .directive('userMessageRow', require('./part/message-row.directive.js'));