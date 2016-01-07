'use strict'
function LoginController($rootScope, $state, Login) {
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.login = login;
  vm.showErrors = showErrors;
  alert(JSON.stringify(storage));
  //check for stay logged
  if (storage.getItem('email') !== null && (storage.getItem('stayLogged') !== 0 && storage.getItem('stayLogged') !== '0')) {
     $state.go('home');
     $rootScope.$broadcast('showActionBar', true);
  }
  else //be sure to clear old storage
    storage.clear();
      
  //functions
  function login() {
    //login function
    Login.login(vm.post, function(data) {
      alert(data['message']);
      if (data['success'] === 1) {
        //successful login
        //set storage items
        storage.setItem('stayLogged', vm.post.stayLogged);
        storage.setItem('email', data['email']);
        storage.setItem('first', data['first']);
        storage.setItem('last', data['last']);
        $rootScope.$broadcast('showActionBar', true);
        $state.go('home');
      }
    });
  } //end login function
  function showErrors() {
    alert("There are errors in the registration form, check input and try again!");
  }
} //end login controller

module.exports = LoginController;
