(function()
{ //wrap
  var storage = window.localStorage;
  //create module controllers
  angular.module('adder')
   //PANDA maybe merge these 3 features together to save code since same concept 
  //friend invite controller
  .controller('FriendInviteController', function($scope, FriendInviter)
  {
    $scope.post = {};
    $scope.post.from = storage.getItem("email");
    $scope.send = function()
    {
      FriendInviter.send($scope.post, function(data)
      {
        alert(data["message"]);   
      });
    };
  }) //end friend invite controller

  //event create controller
  .controller('EventCreateController', function($scope, $filter, Creater)
  {
    $scope.post = {};
    $scope.post.recurring = 0;
    $scope.post.creator = storage.getItem("email");
    $scope.create = function()
    { //start create function
      //form validation
      if ($scope.info.minPart == null)
        $scope.info.minPart = 1;
      if ($scope.info.maxPart == null)
        $scope.info.maxPart = 0;
      if ($scope.info.recType) {
        //code
      }
      $scope.info.startDate = $filter('date')($scope.info.startDate, "yyyy-MM-dd hh:mm:ss");
      $scope.info.endDate = $filter('date')($scope.info.endDate, "yyyy-MM-dd hh:mm:ss"); 
      Creater.create($scope.post, 'event', function(data)
      {
        alert(data["message"]);   
      });
    }; //end create function
  }) //end create event controller
  
  //group create controller
  .controller('GroupCreateController', function($scope, $filter, Creater)
  {
    $scope.post = {};
    $scope.post.creator = storage.getItem("email");
    $scope.create = function()
    { //start create function
      //form validation
      alert(JSON.stringify($scope.post));
      Creater.create($scope.post, 'group', function(data)
      {
        alert(data["message"]);   
      });
    }; //end create function
  }); //end create group controller
})();