'use strict'
module.exports = function($rootScope)
{ //events controller
  var vm = this;
  $rootScope.$broadcast('setTitle', 'Events');
}; //end events controller