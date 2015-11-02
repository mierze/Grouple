'use strict'
module.exports = function($scope, $stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  var storage = window.localStorage;
  //PANDA: need to check rank in group / event so that can show or hide editable
  $scope.init = function()
  { //start init function
    var type = 'group';
    $scope.post = {};
    //case that id is for logged user's email
    if($stateParams.id !== null)
      $scope.post.id = $stateParams.id;
    else
     alert("problem with id passed");
    $scope.post.user = storage.getItem("email");
    ProfileFetcher.fetch($scope.post, type, function(data)
    { //start fetch profile
      alert(data["message"]);
      if (data["success"])
      {
        //PANDA set for now. next get from api
        $scope.editable = true;
        $scope.info = data["info"];
      }
      else //generic catch
        alert(data["message"]);
    }); //end fetch profile
    $scope.post.content = type;
    ImageFetcher.fetch($scope.post, type, function(data)
    { //start fetch image
      if (data["success"])
      {
        var imgUrl = "data:image/png;base64," + data["image"];
        $scope.image = imgUrl;
      }
      else
        //generic catch
        alert(data["message"]);
    }); //end fetch image      
  }; //end init function
  $scope.start = function(type)
  {
    $state.go("user-list", {content: type, id: $scope.post.id});
  };
  //modal functionality below
  $scope.showEditProfile = function()
  {
    document.getElementById('editprofile-modal').style.display = 'block';
  };
  $scope.closeEditProfile = function()
  {
    document.getElementById('editprofile-modal').style.display = 'none';
  };
}; //end profile controller