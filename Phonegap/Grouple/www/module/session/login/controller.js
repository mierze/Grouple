'use strict'
module.exports = function($scope, $state, Login)
{ //login controller
  var storage = window.localStorage;
  $scope.post = {};
  //check for stay logged
  alert(JSON.stringify(storage));
  if (storage.getItem('email') !== null && storage.getItem('stayLogged') !== '0')
    $state.go('home');
  else //be sure to clear old storage
    storage.clear();
  $scope.login = function()
  { //login function
    Login.login($scope.post, function(data)
    {
        if (data['success'] === '1')
        { //successful login
          alert(data['message']);
          //set storage items
          if ($scope.post.stayLogged !== 1)
            $scope.post.stayLogged = '0';
          storage.setItem('stayLogged', $scope.post.stayLogged);
          storage.setItem('email', $scope.post.email);
          storage.setItem('name', 'friend');
          //PANDA: set name here too
          //$state.go('home');
        }
        else //generic catch
          alert(data['message']);
        $state.go('home');
    });
  }; //end login function
}; //end login controller