'use strict';

require('angular');
require('ng-cordova');

angular.module('grouple', [
    require('./module').name,
    require('angular-ui-router')
  ])
  .config(require('./app.routes.js'));