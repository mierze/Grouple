'use strict'
module.exports = function($scope, $stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  //globals for user profile
  var storage = window.localStorage;
  var type = 'user';
  
  //TODO: need to check rank in group / event so that can show or hide editable
  $scope.init = function()
  { //start init function
    $scope.post = {};
    $scope.privs = {};
    $scope.privs.admin = true;
    $scope.showEdit = false;
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
      if (data['success'] === 1)
      { //fetched successfully
        $scope.info = data['info'];
        /*//set title to user's name
        var args = [$scope.info.first, $scope.info.last];
        $scope.$emit('setTitle', args);*/
        //check for unset data
        if ($scope.info.birthday == null)
          $scope.birthdayNull = true;
        else
        { //parse age from birthday
          var birthday = new Date($scope.info.birthday); //to date
          var difference = new Date - birthday;
          $scope.info.age = Math.floor((difference / 1000/*ms*/ / (60/*s*/ * 60/*m*/ * 24/*hr*/) ) / 365.25/*day*/);
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
    ImageFetcher.fetch($scope.post, type, function(data)
    { //start fetch image
      if (data['success'] === 1)
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
    $scope.toggleEdit = function()
    {
      if ($scope.showEdit)
        $scope.showEdit = false;
      else
        $scope.showEdit = true;
    };
  }; //end init function
}; //end profile controller