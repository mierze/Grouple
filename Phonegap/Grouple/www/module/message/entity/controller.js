'use strict'
module.exports = function($scope, $stateParams, MessageFetcher, MessageSender)
{ //entity message controller
  var storage = window.localStorage;
  var type = $stateParams.content;
  $scope.post = {}; //post params for http request
  $scope.init = function()
  { //init function
    //PANDA do error checking
    alert('type is' + type);
    $scope.post.id = $stateParams.id;
    $scope.post.user = storage.getItem("email");
    $scope.post.sender = storage.getItem("email");
    MessageFetcher.fetch($scope.post, type, function(data)
    {
      if (data["success"])
        $scope.messages = data["messages"];
      else
        //PANDA, populate sad guy.
        alert(data["message"]);
    });
  }; //end init function
  $scope.send = function()
  { //start send
    MessageSender.send($scope.post, type, function(data)
    {
      alert(data["message"]);
    });
  }; //end send function
}; //end entity message controller
