(function()
{ //wrap
  //session storage
  var storage = window.localStorage;
  //create module controllers
  angular.module('profile')
  //profile controller
  .controller('ProfileController', function($scope, $stateParams, ProfileFetcher, ImageFetcher)
  {
    //PANDA: need to check rank in group / event so that can show or hide editable
    $scope.init = function(type)
    { //start init function
      //could use stateParams. PANDA
      
      ProfileFetcher.fetch(type, $stateParams.id, function(data)
      {
        if (data["success"])
        {
          alert("SUCCESS FETCHING PROFILE");
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
          alert("Error fetching profile info.\n" + data["message"]);
      });
      
      ImageFetcher.fetch(type, $stateParams.id, function(data)
      {
        if (data["success"])
        {
          var imgUrl = "data:image/png;base64," + data["image"];
          $scope.image = imgUrl;
        }
        else
          //generic catch
          alert(data["message"]);
        
      });
      
    }; //end init function
    //$scope.
  }); //end profile controller
})();