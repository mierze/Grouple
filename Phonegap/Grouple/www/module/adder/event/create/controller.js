'use strict'
module.exports = function($scope, $filter, $state, Creater)
{ //event create controller
  var storage = window.localStorage;
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
      $scope.created = true;
      //PANDA give user option to go to profile or invite groups
     // $state.go("event-profile", {id: data["id"]});
      //PANDA launch event-invite page
      $state.go("event-invite", {id: data["id"]});
    }); //end creater create
  };
}; //end event create controller