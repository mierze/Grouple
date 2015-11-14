'use strict'
module.exports = function($scope, $stateParams, EventInviter, ListFetcher)
{ //event invite controller
  var storage = window.localStorage;
  $scope.post = {};
  $scope.post.id = storage.getItem('email');
  $scope.post.user = storage.getItem('email');
  $scope.invites = {};
  $scope.init = function()
  {
    ListFetcher.fetch($scope.post, /*type of content to grab*/'groups', function(data)
    { //start fetch list of groups to invite
      if (data['success'] === 1)
        $scope.items = data['items'];
      else
        alert(data['message']);
    }); //end fetch list
  };
  $scope.toggleRow = function(id)
  {
    alert("id is " + id);
    if ($scope.invites[id] == null)
      $scope.invites[id] = true;
    else
      $scope.invites[id] = null;
    alert(JSON.stringify($scope.invites));
  };
  $scope.send = function()
  {
    var post = {};
    post.from = storage.getItem("email");
    post.id = $stateParams.id;
    post.invites = $scope.invites;
    alert(JSON.stringify(post));
    EventInviter.send(post, function(data)
    {
      alert(JSON.stringify(data));//data["message"]);
    });
  };
}; //end event invite controller