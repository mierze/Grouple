'use strict'
function SessionChecker($rootScope, $state) {
    var storage = window.localStorage;
    this.check = check;

  return {
    check: this.check
  };

  function check(_in) {
      if ((storage.getItem('logged') != true) && (storage.getItem('logged') != "true")) {
          storage.clear();
          $rootScope.$broadcast('setLogged', false);
          if (_in) //user is trying to get in app
            $state.go('login');
      }
      else {
          setTimeout(function(){
              $rootScope.$broadcast('setLogged', true); }, 300);
              return true;
      }
  }
}

module.exports = SessionChecker;
