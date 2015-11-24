'use strict';
//TODO: change to ngCordova
//require('./node_modules/cordova/cordova.js'); //provides cordova
var angular = require('../node_modules/angular'); //global angular var
require('../node_modules/ui-router/angular-ui-router.js'); //provides 'ui-router'
require('../node_modules/jquery/jquery.min.js'); //provides jquery TODO: don't use jquery
require('./module/part/action-bar/nav.js'); //throw this in controller or similar file

//main app module
angular.module('grouple',
    [ 
        require('./module').name, //app modules
        require('ui-router')
    ])
.config(require('./app.routes.js')); //app routing

