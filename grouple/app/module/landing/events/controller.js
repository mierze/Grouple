'use strict'
function EventsController($rootScope) {
  //events controller
  var vm = this;
  $rootScope.$broadcast('setTitle', 'Events');
}; //end events controller

module.exports = EventsController;