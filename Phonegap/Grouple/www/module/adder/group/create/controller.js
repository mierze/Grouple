'use strict'
module.exports = function($scope, $state, Creater)
{ //group create controller
  var storage = window.localStorage;
  //create module controllers
  $scope.post = {};
  $scope.post.public = "1";
  $scope.created = "false"; //boolean for whether group has been created
  $scope.create = function()
  { //create function
    //form validation  
    $scope.post.creator = storage.getItem("email");
    alert("POST is now:\n"+JSON.stringify($scope.post));
    Creater.create($scope.post, 'group', function(data)
    { //creater create
      if (data["success"])
      { //created group successfully
        alert("1");
        $scope.created = true;
        //PANDA find out if creator is added to group
        //PANDA launch group-invite page
        $state.go("group-invite", {id: data["id"]});
      }
    });
  };
}; //end group create controller