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
          alert(data["message"] + "\n" + JSON.stringify($scope.post));
          //set storage items
          storage.setItem("stayLogged", $scope.post.stayLogged);
  
          storage.setItem("email", $scope.post.email);
          storage.setItem("name", "friend");
          //PANDA: set name here too
          //$state.go('home');
        }
        else //generic catch
          alert(data['message']);
        $state.go('home');
    });
  }; //end login function
}; //end login controller