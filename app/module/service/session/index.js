'use strict';
module.exports = angular.module('service.session', [])
  .factory('Login', require('./login'))
  .factory('Register', require('./register'));
 // .factory('SettingsGetter', require('./settings'));
