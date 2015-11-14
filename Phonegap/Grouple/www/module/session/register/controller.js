'use strict'
module.exports = function($scope, Register, $state)
{ //register controller
  var storage = window.localStorage;
  $scope.post = {};
  $scope.post.last = ''; //default for optional field
  $scope.register = function()
  { //register function
    Register.register($scope.post, function(data)
    { //start register
        alert(data['message']);
        if (data['success'] === 1)
        {
          storage.setItem('email', $scope.post.email);
          storage.setItem('stayLogged', '1');
          storage.setItem('first', $scope.post.first);
          storage.setItem('last', $scope.post.last)
          //launch home
          $state.go('home');
        }
    }); //end register
  }; //end register function
  $scope.showErrors = function()
  {
    alert("There are errors in the form, try again!");
  };
}; //end register controller
