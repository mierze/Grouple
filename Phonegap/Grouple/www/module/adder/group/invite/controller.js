'use strict'
module.exports = function($scope, $stateParams, /*FriendInviter, GROUPINVITER*/ ListFetcher, GroupInviter)
{ //group invite controller
  var storage = window.localStorage;
  $scope.invites = {};
  $scope.items = {};
  $scope.init = function()
  {
    //TODO: should also grab group members and remove those from the friends list and then display that
    var post = {};
    post.id = storage.getItem("email");
    post.user = storage.getItem("email");
    ListFetcher.fetch(post, /*type of content to grab*/'friends', function(data)
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
  $scope.toggleRole = function(id)
  {
    //TODO gather role and change and submit to post
    var roleID = id;
    alert("ID is " + roleID);
   // $(roleID).text('+');
   // $(roleID).text('this');
    if ($scope.invites[id] != null)
    {
      var role = '-';
    }
    else
      var role = 'M';
    //add role under invites unless role is '-'
    if (role !== '-')
      $scope.invites[id] = 'M';
    else
      $scope.invites[id] = null;
    alert(JSON.stringify($scope.invites));
  };
  $scope.send = function()
  {
    var post = {};
    post.id = $stateParams.id;
    post.invites = $scope.invites;
    GroupInviter.send(post, function(data)
    {
      alert(JSON.stringify(data));//data["message"]);
    });
  };
}; //end group invite controller