'use strict';
var angular = require('./node_modules/angular');
require('./node_modules/ui-router/angular-ui-router.js');
console.log(JSON.stringify(angular));
//define main app module
angular.module('grouple', [
    require('./module/service').name,
    require('./module/session').name,
    require('./module/adder').name,
    require('./module/list').name,
    require('./module/profile').name,
    require('./module/message').name,
    'ui.router'
  ])
.config(require('./app.routes.js'))
.controller('NavigationController', require('./module/controller.js'));
