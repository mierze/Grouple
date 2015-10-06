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
      //PANDA
      //launch overlay -> inflate with groups from the  list.fetch service function(post, type=>groups, callback)
      $scope.created = true;
      $state.go("event-profile", {id: data["id"]});
      alert("TRUE NOW");
    }); //end creater create
  };
  //modal functionality below
  $scope.showEventCreate = function()
  {
    document.getElementById('event-create').style.display = 'block';
  };
  $scope.closeEventCreate = function()
  {
    document.getElementById('event-create').style.display = 'none';
  };
}; //end event create controller