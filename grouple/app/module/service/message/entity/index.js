'use strict';
module.exports = angular.module('service.message.entity', [])
  .factory('EntityMessageGetter', require('./get'))
  .factory('EntityMessageSender', require('./send'));
