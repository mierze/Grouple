(function()
{ //wrap
  var storage = window.localStorage;
  //create module controllers
  angular.module('grouple')
  
   //PANDA maybe merge these 3 features together to save code since same concept 
  //add friend controller
  .controller('AddFriendController', function($scope, $http)
  {
    $scope.invite = {};
    $scope.invite.sender = storage.getItem("email");
    //send invite function
    $scope.sendInvite = function()
    { //start send invite function
      $http(
      { //http request to add friend
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/add_friend.php',
        data    : $scope.invite
      }).success(function(data)
      {
        if (data["success"] === 1)
        {
          //successful friend added
          alert(data["message"]);
        }
        else
        {
          //generic catch
          alert("Error fetching profile.\n"+data["message"]);
        }
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end send invite function
  }) //end add friend controller

  //event create controller
  .controller('EventCreateController', function($scope, $http, $filter)
  {
    $scope.info = {};
    $scope.info.recurring = 0;
    $scope.info.creator = storage.getItem("email");
    $scope.create = function()
    { //start create function
      //check all inputs are valid
      if ($scope.info.minPart == null)
        $scope.info.minPart = 1;
      if ($scope.info.maxPart == null)
        $scope.info.maxPart = 0;
      if ($scope.info.recType) {
        //code
      }
      $scope.info.startDate = $filter('date')($scope.info.startDate, "yyyy-MM-dd hh:mm:ss");
      $scope.info.endDate = $filter('date')($scope.info.endDate, "yyyy-MM-dd hh:mm:ss");
      $http(
      { //http request to create event
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/create_event.php',
        data    : $scope.info
      }).success(function(data)
      {
        if (data["success"])
          //successful event create
          alert(data["message"]);
        else
          //generic catch
          alert("Success: " + data["success"] + "\nError creating event.\n" + data["message"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end create function
  }) //end create event controller
  
  //group create controller
  .controller('GroupCreateController', function($scope, $http, $filter)
  {
    $scope.info = {};
    $scope.info.creator = storage.getItem("email");
    $scope.create = function()
    { //start create function
      //check all inputs are valid
      $http(
      { //http request to create group
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/create_group.php',
        data    : $scope.info
      }).success(function(data)
      {
        if (data["success"])
          //successful group create
          alert(data["message"]);
        else
          //generic catch
          alert(data["message"] + "Error: " + data["success"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end create function
  }); //end create group controller
})();