'use strict';
module.exports = angular.module('message.user', [])
  .controller('UserMessageController', require('./controller'))
  .directive('userMessageRow', require('./part/message-row.directive'));