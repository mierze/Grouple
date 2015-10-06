'use strict'
module.exports = function($scope, $state)
{ //navigation controller
    var storage = window.localStorage;
    //function to handling clearing memory and logging out user
    $scope.logout = function()
    {
      storage.clear(); //clear storage
      alert(storage.getItem("email"));
      document.location.href="#login";
      alert("Later playa!");
    };
}; //end navigation controller