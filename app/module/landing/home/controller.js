'use strict'
function HomeController($state, $rootScope) {
    var storage = window.localStorage;
    if (!storage.getItem('logged'))
        $state.go('login');
    else {
        $rootScope.$broadcast('setLogged', true);
        $rootScope.$broadcast('setTitle', 'Grouple');
        $state.go($state.current, {}, {reload: true});
    }
}

module.exports = HomeController;
