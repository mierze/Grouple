'use strict'
module.exports = function($scope, $state)
{ //navigation controller
    var storage = window.localStorage;
    //function to handling clearing memory and logging out user
    $scope.logout = function()
    {
      storage.clear(); //clear storage
      $state.go('login');
      alert('Later ' + storage.getItem('name') + '!');
    };
    //TODO: work on setting title from outside controllers
    $scope.$on('setTitle', function(args)
    {
        alert('emit made it to $on');
    });
}; //end navigation controller