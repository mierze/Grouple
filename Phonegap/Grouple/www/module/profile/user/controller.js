'use strict'
module.exports = function($scope, $stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  var storage = window.localStorage;
  //PANDA: need to check rank in group / event so that can show or hide editable
  $scope.init = function()
  { //start init function
    var type = 'user';
    $scope.post = {};
    //case that id is for logged user's email
    if ($stateParams.id === 'user')   
      $scope.post.id = storage.getItem('email');  
    else if($stateParams.id !== null)   
      $scope.post.id = $stateParams.id;
    else
      alert('Error: invalid id specified!');
    $scope.post.user = storage.getItem('email');
    ProfileFetcher.fetch($scope.post, type, function(data)
    { //start fetch profile
      if (data['success'] === '1')
      { //fetched successfully
        $scope.editable = true; //mod privs for editing
        $scope.info = data['info'];
        /*//set title to user's name
        var args = [$scope.info.first, $scope.info.last];
        $scope.$emit('setTitle', args);*/
        //check for unset data
        if ($scope.info.birthday == null)
          $scope.birthdayNull = true;
        else
        { //parse age from birthday
          var birthday = new Date($scope.info.birthday);
          $scope.info.birthday = birthday;
          var difference = new Date - birthday;
          $scope.info.age = Math.floor( (difference / 1000/*ms*/ / (60/*s*/ * 60/*m*/ * 24/*hr*/) ) / 365.25/*day*/ );
        }
        if ($scope.info.about == null)
          $scope.aboutNull = true;
        if ($scope.info.location == null)
          $scope.locationNull = true;
        //end check for unset data
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    $scope.post.content = type;
    ImageFetcher.fetch($scope.post, type, function(data)
    { //start fetch image
      if (data['success'])
      {
        if (data['image'].length < 10  || data['image'] == null)
          $scope.imageNull = true;
        else
        {
          var imgUrl = 'data:image/png;base64,' + data['image'];
          $scope.image = imgUrl;
        }
      }
      else
      { //generic catch
        $scope.imageNull = true;
        alert(data['message']);
      }
    }); //end fetch image      
  }; //end init function
  $scope.start = function(type)
  {
    $state.go('user-list', {content: type, id: $scope.post.id});
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