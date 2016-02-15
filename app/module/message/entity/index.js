'use strict';
module.exports = angular.module('message.entity', [])
  .controller('EntityMessageController', require('./controller'))
  .directive('entityMessageRow', require('./part/message-row.directive'));