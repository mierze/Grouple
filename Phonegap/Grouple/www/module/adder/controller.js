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
  .controller('EventCreateController', function($scope, $filter, $state, Creater)
  {
    $scope.post = {};
    $scope.created = false; //boolean whether event has been created
    $scope.create = function()
    { //create function
      //form validation
      alert("POST is now:\n"+JSON.stringify($scope.post));
      $scope.post.recurring = 0;
      $scope.post.creator = storage.getItem("email");
      if ($scope.info.minPart == null)
        $scope.info.minPart = 1;
      if ($scope.info.maxPart == null)
        $scope.info.maxPart = 0;
      if ($scope.info.recType) {
        //code
      }
      //PANDA: figure out these dates
      $scope.info.startDate = $filter('date')($scope.info.startDate, "yyyy-MM-dd hh:mm:ss");
      $scope.info.endDate = $filter('date')($scope.info.endDate, "yyyy-MM-dd hh:mm:ss"); 
      Creater.create($scope.post, 'event', function(data)
      { //creater create
        alert(data["message"]);
        //PANDA
        //launch overlay -> inflate with groups from the  list.fetch service function(post, type=>groups, callback)
        $scope.created = true;
        $state.go("event-profile", {id: data["id"]});
        alert("TRUE NOW");
      }); //end creater create
    }; //end create function
  }) //end create event controller
  
  //group create controller
  .controller('GroupCreateController', function($scope, $state, Creater)
  {
    $scope.post = {};
    $scope.created = "false"; //boolean for whether group has been created
    $scope.create = function()
    { //create function
      //form validation  
      $scope.post.creator = storage.getItem("email");
      alert("POST is now:\n"+JSON.stringify($scope.post));
      Creater.create($scope.post, 'group', function(data)
      { //creater create
        alert(data["message"]);
        if (data["success"])
        { //created group successfully
          alert("1");
          $scope.created = true;
          alert("2");
          $state.go("event-profile", {id: data["id"]});
          alert("3 " + data["id"]);
          //PANDA find out if creator is added to group
        }
      }); //end creater create
    }; //end create function
  }); //end group create controller
})(); //end wrap