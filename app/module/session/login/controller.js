'use strict'
function LoginController($rootScope, $state, Login) {
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.login = login;
  vm.showErrors = showErrors;
  vm.enter = enter;
  alert('for developer: '  + JSON.stringify(storage));

  $rootScope.$broadcast('setTitle', 'Login');
  //check for stay logged
  if (storage.getItem('email') !== null && (storage.getItem('stayLogged')
      !== 0 && storage.getItem('stayLogged') !== '0'))
    vm.enter();
  else //be sure to clear old storage
    storage.clear();

  //functions
  function login() {
    Login.login(vm.post, function(data) {
      alert(data['message']);
      if (data['success'] == 1) {
        //successful login
        //set storage items
        storage.setItem('stayLogged', vm.post.stayLogged);
        storage.setItem('email', data['email']);
        storage.setItem('first', data['first']);
        storage.setItem('last', data['last']);
        vm.enter();
      }
    });
  } //end login function
  function showErrors() {
    alert("There are errors in the registration form, check input and try again!");
  }
  function enter() {
    $rootScope.$broadcast('setLogged', true);
    $state.go('home');
  }
} //end login controller

module.exports = LoginController;
