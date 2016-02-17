'use strict';
module.exports = angular.module('service.session', [])
  .factory('Login', require('./login'))
  .factory('Register', require('./register'))
  .factory('SessionChecker', require('./check'));
 // .factory('SettingsGetter', require('./settings'));
