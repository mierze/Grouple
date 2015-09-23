(function() {
  //create module directives
  var storage = window.localStorage; //grab local storage
 
  angular.module('profile')
  //edit user profile directive
  .directive("userEdit", function($http, $filter) {
  return {
    restrict: 'E',
    templateUrl: "module/profile/layout/partial/user-edit.html",
    controller: function()
    {
      this.save = function(info)
      {
        //this.info.email = info.email;
        alert(JSON.stringify(info));
        info.birthday = $filter('date')(info.birthday, "yyyy-MM-dd");
        alert(info.birthday);
        this.url = "http://mierze.gear.host/grouple/api/update_user.php";
        //http request to fetch list from server PANDA refactor out this
        $http(
        {
          method : 'POST',
          url : this.url,
          data : info
        }).success(function(data)
        {
          if (data["success"] === 1)
          {
            alert("Successfully updated user profile!");
          }
          else if (data["success"] === 0)
          {
            //PANDA, populate sad guy.
            alert(data["message"]);
            alert(JSON.stringify(data["statement"]));
          }
          else
          {
            //generic catch
            alert(data["message"]);
            alert(data["statement"]);
            alert("Error updating user profile.");
          }
        })
        .error(function(data)
        {
          alert("Error contacting server.");
        });
      };
    },
    controllerAs: "userEdit"
    };
  }) //end edit user profile directive
  
  //edit group profile directive
  .directive("groupEdit", function($http) {
  return {
    restrict: 'E',
    templateUrl: "module/profile/layout/partial/group-edit.html",
    controller: function()
    {
      this.save = function(info)
      {
        this.url = "http://mierze.gear.host/grouple/api/update_group.php";
        alert(JSON.stringify(info));
        //http request to fetch list from server PANDA refactor out this
        $http(
        {
          method : 'POST',
          url : this.url,
          data : info
        }).success(function(data)
        {
          if (data["success"] === 1)
          {
            alert("Successfully updated group profile!");
          }
          else if (data["success"] === 0)
          {
            //PANDA, populate sad guy.
            alert("000");
            alert(data["message"]);
          }
          else
          {
            //generic catch
            alert(data["message"]);
            alert("Error updating group profile.");
          }
        })
        .error(function(data)
        {
          alert("Error contacting server.");
        });
      };
    },
    controllerAs: "groupEdit"
    };
  }) //end edit group profile directive
  
  //edit event profile directive
  .directive("eventEdit", function($http, $filter) {
  return {
    restrict: 'E',
    templateUrl: "module/profile/layout/partial/event-edit.html",
    controller: function()
    {
      this.save = function(info)
      {
        info.startDate = $filter('date')(info.startDate, "yyyy-MM-dd hh:mm:ss");
        info.endDate = $filter('date')(info.endDate, "yyyy-MM-dd hh:mm:ss");
        this.url = "http://mierze.gear.host/grouple/api/update_event.php";
        alert(JSON.stringify(info));
        //http request to fetch list from server PANDA refactor out this
        $http(
        {
          method : 'POST',
          url : this.url,
          data : info
        }).success(function(data)
        {
          if (data["success"] === 1)
          {
            alert("Successfully updated event profile!");
          }
          else if (data["success"] === 0)
          {
            //PANDA, populate sad guy.
            alert(data["message"]);
          }
          else
          {
            //generic catch
            alert(data["message"]);
            alert("Error updating event profile.");
          }
        })
        .error(function(data)
        {
          alert("Error contacting server.");
        });
      };
    },
    controllerAs: "eventEdit"
    };
  }); //end edit event profile directive
})();