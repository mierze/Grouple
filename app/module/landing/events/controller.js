'use strict'
function EventsController($rootScope) {
  $rootScope.$broadcast('setTitle', 'Events');
}

module.exports = EventsController;