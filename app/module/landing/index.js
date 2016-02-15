'use strict';
module.exports = angular.module('landing', [
  require('./home').name,
  require('./friends').name,
  require('./groups').name,
  require('./events').name
]);