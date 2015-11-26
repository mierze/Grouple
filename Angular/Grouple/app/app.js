'use strict';

require('angular'); //global angular var
require('ng-cordova'); //provides cordova
var $ = require('../node_modules/jquery/jquery.min.js'); //provides jquery TODO: don't use jquery

//main app module
angular.module('grouple', [
    require('./module').name,
    require('angular-ui-router')
])
.config(require('./app.routes.js')); //app routing

