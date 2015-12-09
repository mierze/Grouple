'use strict'
module.exports = function($rootScope)
{ //groups controller
  var vm = this;
  $rootScope.$broadcast('setTitle', 'Groups');
}; //end groups controller