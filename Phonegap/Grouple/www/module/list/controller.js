'use strict'
module.exports = function($scope, $stateParams, ListFetcher)
{ //list controller
  var storage = window.localStorage;
  if ($stateParams.content != null)
  { //ensure content is set
    if ($stateParams.content === "friend_invites")
    { //editable check
      $scope.editable = true;
    }
    //prepare post parameters
    $scope.post = {};
    //PANDA switch content to type
    if ($stateParams.id == null || ($stateParams.id.length < 2 && ($stateParams.content === 'user' || $stateParams.content === 'badges')))
      $scope.post.id = storage.getItem("email");
    else
      $scope.post.id = $stateParams.id;
    $scope.post.user = storage.getItem("email");
    //PANDA: prepare for badge row items
    //fetch data and wait for callback
    ListFetcher.fetch($scope.post, $stateParams.content, function(data)
    { //start fetch list
      if (data["success"])
      {
       // alert(JSON.stringify(data["items"]));
        $scope.items = data["items"];
      }
      else
        //PANDA, populate sad guy.
        alert(data["message"]);
    }); //end fetch list
  }
  else //error loading page
    alert("Error loading list, please try again!");  
}; //end list controller
