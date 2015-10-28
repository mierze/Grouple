'use strict'
module.exports = function($scope,/* GroupInviter*/ ListFetcher)
{ //event invite controller
  var storage = window.localStorage;
  $scope.post = {};
  $scope.post.id = storage.getItem("email");
  $scope.post.user = storage.getItem("email");
  $scope.init = function()
  {
    ListFetcher.fetch($scope.post, /*type of content to grab*/'groups', function(data)
    { //start fetch list of groups to invite
      if (data["success"])
      {
        alert(JSON.stringify(data["items"]));
        $scope.items = data["items"];
      }
      else
        alert(data["message"]);
    }); //end fetch list
  }
  $scope.send = function()
  {
  };

}; //end event invite controller