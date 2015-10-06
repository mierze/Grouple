'use strict'
module.exports = function($scope, $state, Creater)
{ //wrap
  var storage = window.localStorage;
  //create module controllers
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
    });
  };
}; //end wrap