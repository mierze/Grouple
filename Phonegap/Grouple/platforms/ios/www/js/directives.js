(function() {
  //create module directives
  var storage = window.localStorage; //grab local storage
 
  angular.module('directives', [])
  //edit user profile directive
  .directive("eventCreate", function() {
  return {
    restrict: 'E',
    templateUrl: "template/event-create.html",
    controller: function()
    {
      this.create = function()
      {
        alert("SAVE HERE");
      };
    },
    controllerAs: "eventCreate"
    };
  }) //end edit user profile directive
  
  //edit user profile directive
  .directive("userEdit", function($http, $filter) {
  return {
    restrict: 'E',
    templateUrl: "template/user-edit.html",
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
    templateUrl: "template/group-edit.html",
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
    templateUrl: "template/event-edit.html",
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
  }) //end edit event profile directive
  
  .directive("userRow", function($http) {
    return {
      restrict: 'E',
      templateUrl: "template/user-row.html",
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
    templateUrl: "template/group-row.html",
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
    templateUrl: "template/event-row.html",
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
  }) //end event row directive
  
  //message row directive
  .directive("messageRow", function() {
    return {
      restrict: 'E',
      templateUrl: "template/message-row.html",
      controller: function()
      {
        this.profile = function(email)
        {
          document.location.href="user-profile.html?email="+email;
        };
      },
      controllerAs: "msgCtrl"
    };
  }) //end message row directive
  
  //contact row directive
  .directive("contactRow", function() {
    return {
      restrict: 'E',
      templateUrl: "template/contact-row.html",
      controller: function()
      {
        this.startMessages = function(contact)
        {
          var email = (contact.sender === storage.getItem("email")) ? contact.receiver : contact.sender;
          document.location.href = "messages.html?email=" + email;
        };
        this.imgEnc = function(image)
        {
          return "data:image/png;base64," + image;
        }
      },
      controllerAs: "contactCtrl"
    };
  }); //end contact row directive
})();