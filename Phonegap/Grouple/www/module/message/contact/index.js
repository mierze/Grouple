'use strict';
module.exports = angular.module('message.contact', [])
.controller('ContactController', require('./controller.js'))
.directive('contactRow', require('./directive.js'));