'use strict'
function RegisterController(Register, $state, $rootScope, SessionChecker) {
    var vm = this;
    var storage = window.localStorage;
    vm.post = {};
    vm.post.last = ''; //default for optional field
    vm.register = register;
    vm.showErrors = showErrors;
    vm.enter = enter;

    if (SessionChecker.check(0))
        vm.enter();
    else $rootScope.$broadcast('setTitle', 'Login');

    //functions
    function register() {
        Register.register(vm.post, function register(data) {
            alert(data['message']);
            if (data['success'] == 1) {
                storage.setItem('email', vm.post.email);
                storage.setItem('stayLogged', '1');
                storage.setItem('first', vm.post.first);
                storage.setItem('last', vm.post.last);
                vm.enter();
            }
        }); //end register
    } //end register function
    function showErrors() {
        alert("There are errors in the registration form, check input and try again!");
    }

    function enter() {
        //launch home
        $rootScope.$broadcast('setLogged', true);
        $state.go('home');
    }
} //end register controller

module.exports = RegisterController;
