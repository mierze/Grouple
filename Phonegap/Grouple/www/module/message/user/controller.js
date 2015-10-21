'use strict'
module.exports = function($scope, $stateParams, MessageFetcher, MessageSender)
{ //user message controller
  var storage = window.localStorage;
  $scope.post = {}; //post params for http request
  $scope.init = function()
  { //init function
    $scope.post.contact = $stateParams.id;
    $scope.post.user = storage.getItem("email");
    MessageFetcher.fetch($scope.post, 'user', function(data)
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
    MessageSender.send($scope.post, 'user', function(data)
    {
      alert(data["message"]);
    });
  }; //end send function
}; //end user message controller
