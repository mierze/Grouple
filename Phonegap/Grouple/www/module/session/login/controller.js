'use strict'
module.exports = function($scope, $state, Login)
{ //login controller
  var storage = window.localStorage;
  $scope.post = {};
  //check for stay_logged
  alert(JSON.stringify(storage));
  if (storage.getItem("email") !== null && storage.getItem("stayLogged") === 1)
    $state.go('home');
  $scope.login = function()
  { //login function
    //PANDA form validation
    Login.login($scope.post, function(data)
    {
        if (data["success"])
        { //successful login
          alert(data["message"]);
          //set storage items
          if ($scope.post.stayLogged)
            storage.setItem("stayLogged", 1);
          else
            storage.setItem("stayLogged", 0);
          storage.setItem("email", $scope.post.email);
          //PANDA: set name here too
          $state.go('home');
        }
        else //generic catch
          alert(data["message"]);
    });
  }; //end login function
}; //end login controller