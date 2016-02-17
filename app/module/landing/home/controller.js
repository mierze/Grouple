'use strict'
function HomeController($rootScope, SessionChecker) {
    SessionChecker.check(1);
    $rootScope.$broadcast('setTitle', 'Grouple');
}

module.exports = HomeController;
