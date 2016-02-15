'use strict';
module.exports = angular.module('service.message.user', [])
  .factory('UserMessageGetter', require('./get'))
  .factory('UserMessageSender', require('./send'))
  .factory('ContactGetter', require('./contacts'));
