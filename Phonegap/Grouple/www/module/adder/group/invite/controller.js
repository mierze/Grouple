'use strict'
module.exports = function($scope,/*$stateParams, ?FriendInviter, GROUPINVITER*/ ListFetcher)
{ //group invite controller
  var storage = window.localStorage;
  $scope.post = {};
  $scope.post.id = storage.getItem("email");
  $scope.post.user = storage.getItem("email");
  //PANDA find out if different api for group invites, need to remove friends that are already in group
  //in this case of just creating group
  //no friends will be in it
  //otherwise should have group members loaded in already, could pass it in state params
  $scope.init = function()
  {
    alert("IN INIT!");
    ListFetcher.fetch($scope.post, /*type of content to grab*/'friends', function(data)
    { //start fetch list of groups to invite
      if (data["success"])
      {
        alert(JSON.stringify(data));
        $scope.items = data["items"];
      }
      else
        alert(data["message"]);
    }); //end fetch list
  };
 
  $scope.send = function()
  {
    //loop thru each row
    //for each checked, send invite
    $scope.post = {};
    $scope.post.from = storage.getItem("email");
   // FriendInviter.send($scope.post, function(data)
    //{
   //   alert(data["message"]);
    //});
  };
}; //end group invite controller