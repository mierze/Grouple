'use strict'
module.exports = function($scope, Register, $state)
{ //register controller
  var storage = window.localStorage;
  $scope.post = {};
  $scope.register = function()
  { //register function
    Register.register($scope.post, function(data)
    { //start register
        alert(data["message"]);
        if (data["success"])
        {
          storage.setItem("email", $scope.post.email);
          storage.setItem("name", $scope.post.first + ' ' + $scope.post.last);
          //launch home
          $state.go("home");
        }
    }); //end register
  }; //end register function
}; //end register controller
