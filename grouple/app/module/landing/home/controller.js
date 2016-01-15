'use strict'
function HomeController($rootScope) {
  $rootScope.$broadcast('setTitle', 'Grouple');
}

module.exports = HomeController;