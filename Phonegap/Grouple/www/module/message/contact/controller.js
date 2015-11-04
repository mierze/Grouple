'use strict'
module.exports = function($scope, MessageFetcher)
{ //contact controller
  var storage = window.localStorage;
  $scope.post = {}; //post params for http request
  $scope.post.email = storage.getItem('email');
  $scope.post.user = storage.getItem('email');
  MessageFetcher.fetch($scope.post, 'contacts', function(data)
  {
    if (data['success'])
      $scope.contacts = data['contacts'];
    else
      //PANDA, populate sad guy.
      alert(data['message']);
  });
}; //end contact controller