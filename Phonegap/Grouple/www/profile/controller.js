(function()
{ //wrap
  var storage = window.localStorage;
  //create module controllers
  angular.module('profile')
  
  //profile controller
  .controller('ProfileController', function($scope, $http)
  {
    alert("HERE");
    //PANDA: need to check rank in group / event so that can show or hide editable
    $scope.init = function(content)
    { //start init function
      $scope.post = {};
      $scope.url = "http://mierze.gear.host/grouple/api/get_" + content + "_info.php";
      $scope.post.content = content;
      $scope.editable = false; //dissalow editable priv
      if (getVal("id") != null)
      {
        $scope.post.id = getVal("id");
        alert("solid so far id is " + getVal("id"));
      }
      else if (getVal("email") != null)
      {
        $scope.post.email = getVal("email");
        $scope.info.age = "23";
      }
      else
      {
         $scope.post.email = storage.getItem("email");
         alert("EMAIL is " + $scope.post.email + " " + $scope.url);
         $scope.editable = true;
         $scope.info.age = "23";
      }
      $http(
      {  //http request to fetch user data
        method  : 'POST',
        url     : $scope.url,
        data    : $scope.post
      }).success(function(data)
      {
        alert("SUCCESS");
        if (data["success"])
        {
          //PANDA fix date
          $scope.info = data["info"];
          alert($scope.info.birthday);
          if ($scope.info.birthday !== null)
            $scope.info.birthday = new Date(2013, 9, 22);
          //PANDA Age conversion
        }
        else{
          //generic catch
          alert("ERROR");
          alert("Error fetching profile info.\n" + data["message"]);
        }
        
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
      alert("STILL GOING");
      //http request to get user image
      //PANDA: need to tweak php for content types, make this accept group/event/user and also sizing, seperate into a service
      /*$http(
      {
        method : 'POST',
        url : 'http://mierze.gear.host/grouple/api/get_profile_image_hdpi.php',
        data : $scope.post
      }).success(function(data)
      {
        if (data["success"] === 1)
        {
          var imgUrl = "data:image/png;base64," + data["image"];
          $scope.info.image = imgUrl;
        }
        else
        {
          //generic catch
          alert("Error fetching image.\n"+data["message"]);
        }
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });*/
    }; //end init function
  }); //end profile controller

  alert("end user cont");
})();