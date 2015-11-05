'use strict'
module.exports = function($scope, $stateParams, $state, MessageFetcher, MessageSender)
{ //user message controller
  var type = 'user';
  var storage = window.localStorage;
  $scope.post = {}; //post params for http request
  $scope.init = function()
  { //init function
    $scope.post.contact = $stateParams.id;
    $scope.post.user = storage.getItem('email');
    MessageFetcher.fetch($scope.post, type, function(data)
    {
      if (data['success'] === 1)
        $scope.messages = data['messages'];
      else
        //TODO, populate sad guy.
        alert(data['message']);
    });
  }; //end init function
  $scope.send = function()
  { //start send
    MessageSender.send($scope.post, type, function(data)
    {
        $state.go('user-messages', {id: $scope.post.contact}, {reload: true});
        
    });
  }; //end send function
}; //end user message controller
