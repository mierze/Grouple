/* TODO list                               * date
 * [ ] Security token in api               *
 * [ ] Remove excess requires in this file *
 *******************************/
'use strict';

//require('./node_modules/cordova/cordova.js'); //provides cordova
var angular = require('./node_modules/angular'); //global angular var
require('./node_modules/ui-router/angular-ui-router.js'); //provides 'ui-router'
//require('./node_modules/angularjs-scroll-glue/src/scrollglue.js'); //provides 'luegg.directives'
require('./node_modules/jquery/jquery.min.js'); //provides jquery
require('./module/part/nav.js'); //throw this in controller or similar file

//define main app module
angular.module('grouple', [
    require('./module/service').name,
    require('./module/session').name,
    require('./module/list').name,
    require('./module/adder').name,
    require('./module/profile').name,
    require('./module/message').name,
    require('ui-router')
//    'luegg.directives' //for scroll glue, needs debug
  ])
.config(require('./app.routes.js'))
.directive('actionBar', require('./module/part/action-bar.directive.js'));
