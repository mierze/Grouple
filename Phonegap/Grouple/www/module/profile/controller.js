(function()
{ //wrap
  //session storage
  var storage = window.localStorage;
  angular.module('profile')
  .controller('ProfileController', function($scope, $stateParams, $state, ProfileFetcher, ImageFetcher)
  { //start profile controller
    //PANDA: need to check rank in group / event so that can show or hide editable
    $scope.init = function(type)
    { //start init function
      $scope.post = {};
      if ($stateParams.id.length < 2 && type === 'user')
        //case that id is for logged user's email
        $scope.post.id = storage.getItem("email");
      else
        $scope.post.id = $stateParams.id;
      $scope.post.user = storage.getItem("email");
      ProfileFetcher.fetch($scope.post, type, function(data)
      { //start fetch profile
        if (data["success"])
        {
          //PANDA set for now. next get from api
          $scope.editable = true;
          //PANDA fix date
          $scope.info = data["info"];
          if (type === 'user')
          {
            $scope.info.age = 23;
            if ($scope.info.birthday !== null)
              $scope.info.birthday = new Date(2013, 9, 22);
          }
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
  }); //end profile controller
})();