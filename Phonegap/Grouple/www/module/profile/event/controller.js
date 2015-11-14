'use strict'
module.exports = function($scope, $stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  var storage = window.localStorage;
  $scope.privs = {};
  //TODO: fetch privs
  $scope.privs.admin = true;
  $scope.privs.mod = true;
  $scope.showEdit = false;
  $scope.init = function()
  { //start init function
    var type = 'event'; //type of profile
    $scope.post = {};
    //case that id is for logged user's email
  
    if($stateParams.id !== null)
      $scope.post.id = $stateParams.id;
    else
     alert('problem with id passed');
    $scope.post.user = storage.getItem('email');
    ProfileFetcher.fetch($scope.post, type, function(data)
    { //start fetch profile
      if (data['success'])
      {
        //PANDA set for now. next get from api
        $scope.editable = true;
        $scope.info = data['info'];
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    $scope.post.content = type;
    ImageFetcher.fetch($scope.post, type, function(data)
    { //start fetch image
      if (data['success'] === 1)
      {
        var imgUrl = 'data:image/png;base64,' + data['image'];
        $scope.image = imgUrl;
      }
      else
        //generic catch
        alert(data['message']);
    }); //end fetch image      
  }; //end init function
  $scope.toggleEdit = function()
  {
    if ($scope.showEdit) 
      $scope.showEdit = false;
    else
      $scope.showEdit = true;
  };
}; //end profile controller