'use strict'
function HomeController($rootScope) {
  //home controller
  var vm = this;
  $rootScope.$broadcast('setTitle', 'Grouple');
}; //end home controller

module.exports = HomeController;