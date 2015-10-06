'use strict'
module.exports = function($scope, Register)
{ //register controller
  $scope.post = {};
  $scope.register = function()
  { //register function
    Register.register($scope.post, function(data)
    { //start register
        alert(data["message"]);
    }); //end register
  }; //end register function
}; //end register controller
