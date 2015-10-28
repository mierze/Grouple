/* PANDA list                   * date
 * [ ] Side navigation bugs     * 10/20
 * [ ] Message styling          * 10/21
 * [ ] Security in php(token)   *
 *******************************/
'use strict';
var angular = require('./node_modules/angular');
require('./node_modules/ui-router/angular-ui-router.js'); //provides 'ui-router'
//define main app module
angular.module('grouple', [
    require('./module/service').name,
    require('./module/session').name,
    require('./module/list').name,
    require('./module/adder').name,
    require('./module/profile').name,
    require('./module/message').name,
    require('ui-router')
  ])
.config(require('./app.routes.js'))
.controller('NavigationController', require('./module/controller.js'));
