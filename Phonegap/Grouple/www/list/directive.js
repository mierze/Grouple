(function() {
  //create module directives
  var storage = window.localStorage; //grab local storage
 
  angular.module('directive')
  //edit user profile directive
  .directive("userRow", function($http) {
    return {
      restrict: 'E',
      templateUrl: "list/partial/user-row.html",
      controller: function()
      {
        this.profile = function(email)
        {
          document.location.href="user-profile.html?email="+email;
        };
        this.decision = function(item, type)
        { //start decision
          if (type === "decline")
            this.url = "http://mierze.gear.host/grouple/api/delete_friend.php";
          else
            this.url = "http://mierze.gear.host/grouple/api/accept_friend.php";
          item.sender = item.email;
          item.receiver = storage.getItem("email");
          $http(
          {
            method : 'POST',
            url : this.url,
            data : item
          }).success(function(data)
          {
            if (data["success"] === 1)
            {
              alert("Accepted or declined user!");
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
              alert("Error accepting or declining user.");
            }
          })
          .error(function(data)
          {
            alert("Error contacting server.");
          });   
        }; //end decision
      },
      controllerAs: "user"
    };
  }) //end user row directive
  
  //group row directive
  .directive("groupRow", function() {
  return {
    restrict: 'E',
    templateUrl: "list/partial/group-row.html",
    controller: function()
    {
      this.profile = function(id)
      {
        document.location.href="group-profile.html?id="+id;
      };
        this.decision = function(item, type)
        { //start decision
          if (type === "decline")
            this.url = "http://mierze.gear.host/grouple/api/leave_group.php";
          else
            this.url = "http://mierze.gear.host/grouple/api/accept_group.php";
          item.sender = item.email;
          //PANDA item.id
          item.receiver = storage.getItem("email");
          $http(
          {
            method : 'POST',
            url : this.url,
            data : item
          }).success(function(data)
          {
            if (data["success"] === 1)
            {
              alert(data["message"]);
              alert("Accepted or declined group invite!");
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
              alert("Error accepting or declining group invite.");
            }
          })
          .error(function(data)
          {
            alert("Error contacting server.");
          });   
        }; //end decision
    },
    controllerAs: "group"
    };
  }) //end group row directive
  
  //event row directive
  .directive("eventRow", function() {
  return {
    restrict: 'E',
    templateUrl: "list/partial/event-row.html",
    controller: function()
    {
      this.profile = function(id)
      {
        document.location.href="event-profile.html?id="+id;
      };
      this.decision = function(item, type)
      { //start decision
        if (type === "decline")
          this.url = "http://mierze.gear.host/grouple/api/leave_event.php";
        else
          this.url = "http://mierze.gear.host/grouple/api/accept_event.php";
        item.sender = item.email;
        //PANDA item.id
        item.receiver = storage.getItem("email");
        $http(
        {
          method : 'POST',
          url : this.url,
          data : item
        }).success(function(data)
        {
          if (data["success"] === 1)
          {
            alert(data["message"]);
            alert("Accepted or declined event invite!");
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
            alert("Error accepting or declining event invite.");
          }
        })
        .error(function(data)
        {
          alert("Error contacting server.");
        });   
      } //end decision
    },
    controllerAs: "event"
    };
  }); //end event row directive
})();