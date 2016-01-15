'use strict'
function RegisterController(Register, $state) {
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.post.last = ''; //default for optional field
  vm.register = register;
  vm.showErrors = showErrors;
  //functions
  function register() {
    Register.register(vm.post, function(data) {
        alert(data['message']);
        if (data['success'] === 1) {
          storage.setItem('email', vm.post.email);
          storage.setItem('stayLogged', '1');
          storage.setItem('first', vm.post.first);
          storage.setItem('last', vm.post.last)
          //launch home
          $state.go('home');
        }
    }); //end register
  } //end register function
  function showErrors() {
    alert("There are errors in the registration form, check input and try again!");
  }
} //end register controller

module.exports = RegisterController;
