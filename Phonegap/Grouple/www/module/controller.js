'use strict'
module.exports = function($scope, $state)
{ //navigation controller
    var storage = window.localStorage;
    //function to handling clearing memory and logging out user
    $scope.logout = function()
    {
      storage.clear(); //clear storage
      document.location.href="#login";
      alert("Later " + storage.getItem("name") + "!");
    };
}; //end navigation controller