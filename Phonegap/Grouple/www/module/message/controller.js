module.exports = function()
{ //wrap
  var storage = window.localStorage;
  angular.module('message')
  .controller('ContactController', function($scope, MessageFetcher)
  { //start contact controller
    $scope.post = {}; //post params for http request
    $scope.post.email = storage.getItem("email");
    $scope.post.user = storage.getItem("email");
    MessageFetcher.fetch($scope.post, 'contacts', function(data)
    {
      if (data["success"])
        $scope.contacts = data["contacts"];
      else
        //PANDA, populate sad guy.
        alert(data["message"]);
    });
  }) //end contact controller
   
  .controller('MessageController', function($scope, $stateParams, MessageFetcher, MessageSender)
  { //start message controller
    $scope.post = {}; //post params for http request
    $scope.init = function(type)
    { //start init function
      if (type === "user") //PANDA could be post.id if we want to unify all
        $scope.post.contact = $stateParams.id;
      else
      { //group / event message
        //set post params
        if ($stateParams.id != null && $stateParams.id.length > 2)
          $scope.post.id = $stateParams.id;
        else
          alert("ERROR");
      }
      $scope.post.user = storage.getItem("email");
      MessageFetcher.fetch($scope.post, type, function(data)
      {
        if (data["success"])
          $scope.messages = data["messages"];
        else
          //PANDA, populate sad guy.
          alert(data["message"]);
      });
    }; //end init function
    $scope.send = function(type)
    { //start send
      MessageSender.send($scope.post, type, function(data)
      {
        alert(data["message"]);
      });
    }; //end send function
  }); //end message controller
};