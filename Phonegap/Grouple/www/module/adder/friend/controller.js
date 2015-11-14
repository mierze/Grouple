'use strict'
module.exports = function($scope, FriendInviter)
{ //wrap
  var storage = window.localStorage;
  //create module controll 
  $scope.post = {};
  $scope.showAddFriend = false;
  $scope.post.from = storage.getItem('email');
  $scope.send = function()
  {
    FriendInviter.send($scope.post, function(data)
    {
      alert(data['message']);
    });
  };
  $scope.toggleAddFriend = function()
  {
    if ($scope.showAddFriend) 
      $scope.showAddFriend = false;
    else
      $scope.showAddFriend = true;
  };
}; //end wrap