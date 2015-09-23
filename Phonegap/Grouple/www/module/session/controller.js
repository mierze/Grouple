(function()
{ //wrap
  var storage = window.localStorage;
  angular.module('session')
  //PANDA add setting controller
    //login controller
  .controller('LoginController', function($scope, $http, $state)
  {
    $scope.user = {};
    //check for stay_logged
    if (storage.getItem("email") != null && storage.getItem("stayLogged"))
    {
      $state.go('home');
    }
    //login function
    $scope.login = function()
    {
      $http(
      { //http request to attempt login
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/login.php',
        data    : $scope.user
      }).success(function(data)
      {
        alert("1");
        if (data["success"] === 1)
        { //successful login
          alert(data["message"]);
          //set storage items
          if ($scope.user.stayLogged)
            storage.setItem("stayLogged", true);
          else
            storage.setItem("stayLogged", false);
          storage.setItem("email", $scope.user.email);
          //PANDA: set name here too
          $state.go('home');
        }
        else //generic catch
          alert(data["message"] + " Error: " + data["success"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end login function
  }) //end login controller
  
  //register controller
  .controller('RegisterController', function($scope, $http)
  {
    $scope.user = {};
    //register function
    $scope.register = function()
    {
      $http(
      { //http request to register account
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/register.php',
        data    : $scope.user
      }).success(function(data)
      {
        if (data["success"])
          //successful register
          alert(data["message"]);
        else //generic catch
          alert("Error registering account.");
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end register function
  }); //end register controller
})();