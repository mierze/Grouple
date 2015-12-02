'use strict'
module.exports = function($rootScope)
{ //home controller
  var vm = this;
  $rootScope.$broadcast('setTitle', 'Grouple');
}; //end home controller