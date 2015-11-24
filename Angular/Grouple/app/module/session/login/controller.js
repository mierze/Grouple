'use strict'
module.exports = function($state, Login)
{ //login controller
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.login = login;
  //check for stay logged
  alert(JSON.stringify(storage));
  if (storage.getItem('email') !== null && (storage.getItem('stayLogged') !== 0 && storage.getItem('stayLogged') !== '0'))
    $state.go('home');
    //$state.go('event-invite', {id: '98'});
  else //be sure to clear old storage
    storage.clear();
    
  //functions
  function login()
  { //login function
    Login.login(vm.post, function(data)
    {
      alert(data['message']);
      if (data['success'] === 1)
      { //successful login
        //set storage items
        storage.setItem('stayLogged', vm.post.stayLogged);
        storage.setItem('email', data['email']);
        storage.setItem('first', data['first']);
        storage.setItem('last', data['last']);
        $state.go('home');
      }
    });
  }; //end login function
}; //end login controller