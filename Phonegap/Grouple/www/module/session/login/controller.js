'use strict'
module.exports = function($scope, $state, Login)
{ //login controller
  var storage = window.localStorage;
  $scope.post = {};
  $scope.post.stayLogged = 0;
  //check for stay logged
  alert(JSON.stringify(storage));
  if (storage.getItem('email') !== null && (storage.getItem('stayLogged') !== 0 && storage.getItem('stayLogged') !== '0'))
    $state.go('home');
  else //be sure to clear old storage
    storage.clear();
  $scope.login = function()
  { //login function
    Login.login($scope.post, function(data)
    {
        if (data['success'] === 1)
        { //successful login
          alert(data['message']);
          //set storage items
          storage.setItem('stayLogged', $scope.post.stayLogged);
          storage.setItem('email', $scope.post.email);
          storage.setItem('name', 'friend');
          $state.go('home');
        }
        else //generic catch
          alert(data['message']);
    });
  }; //end login function
}; //end login controller