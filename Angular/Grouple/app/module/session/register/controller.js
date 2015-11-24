'use strict'
module.exports = function(Register, $state)
{ //register controller
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.post.last = ''; //default for optional field
  vm.register = register;
  vm.cake = 'ake';
 // vm.showErrors = showErrors;
  alert(JSON.stringify(vm.post));
  //functions
  function register()
  { //register function
    alert('in register post is' + JSON.stringify(vm.post));
    Register.register(vm.post, function(data)
    { //start register
        alert(data['message']);
        if (data['success'] === 1)
        {
          storage.setItem('email', vm.post.email);
          storage.setItem('stayLogged', '1');
          storage.setItem('first', vm.post.first);
          storage.setItem('last', vm.post.last)
          //launch home
          $state.go('home');
        }
    }); //end register
  }; //end register function
  function showErrors()
  {
    alert("There are errors in the form, try again!");
  };
}; //end register controller
