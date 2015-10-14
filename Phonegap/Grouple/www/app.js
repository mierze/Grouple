/* PANDA list                   * date
 * [ ] Badge lists              * 10/15
 *      -make ui-sref pass badges / email
 * [ ] Side navigation bugs     * 10/20
 * [ ] Message styling          * 10/21
 * [ ] Security in php(token)   *
 *******************************/
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
    require('ui-router')
  ])
.config(require('./app.routes.js'))
.controller('NavigationController', require('./module/controller.js'));
