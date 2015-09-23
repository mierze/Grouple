(function()
{ //wrap
  var storage = window.localStorage;
  angular.module('session')
  //PANDA add setting controller
    //login controller
  .controller('LoginController', function($scope, $state, Login)
  {
    $scope.post = {};
    //check for stay_logged
    if (storage.getItem("email") != null && storage.getItem("stayLogged"))
      $state.go('home');
    $scope.login = function()
    { //login function
      Login.login($scope.post, function(data)
      {
          if (data["success"])
          { //successful login
            alert(data["message"]);
            //set storage items
            if ($scope.post.stayLogged)
              storage.setItem("stayLogged", true);
            else
              storage.setItem("stayLogged", false);
            storage.setItem("email", $scope.post.email);
            //PANDA: set name here too
            $state.go('home');
          }
          else //generic catch
            alert(data["message"]);
      });
    }; //end login function
  }) //end login controller
  
  .controller('RegisterController', function($scope, Register)
  { //register controller
    $scope.user = {};
    //register function
    $scope.register = function()
    {
      Register.register($scope.user, function(data)
      { //start register
          alert(data["message"]);
      }); //end register
    }; //end register function
  }); //end register controller
})();