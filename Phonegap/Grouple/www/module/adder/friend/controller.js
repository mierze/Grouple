'use strict'
module.exports = function($scope, FriendInviter)
{ //wrap
  var storage = window.localStorage;
  //create module controll 
  $scope.post = {};
  $scope.post.from = storage.getItem("email");
  $scope.send = function()
  {
    FriendInviter.send($scope.post, function(data)
    {
      alert(data["message"]);
    });
  };
  //modal functionality below
  $scope.showAddFriend = function()
  {
    document.getElementById('addfriend-modal').style.display = 'block';
  };
  $scope.closeAddFriend = function()
  {
    document.getElementById('addfriend-modal').style.display = 'none';
  };
}; //end wrap