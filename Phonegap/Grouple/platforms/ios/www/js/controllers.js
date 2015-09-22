(function()
{ //wrap
  var storage = window.localStorage;
  //create module controllers
  angular.module('controllers', ['services'])
    
  /*********************************************
  ************* CONTROLLERS BELOW **************
  *********************************************/
  //list controller, serves as the controller for all list pages
  .controller('ListController', function($scope, ListFetcher)
  {
    alert("List controller");
    if (getVal("content") != null)
    { //ensure content is set
      if (getVal("content") === "friend_invites")
      { //editable check
        $scope.editable = true;
      }
      //fetch data and wait for callback
      alert("a to call");
      ListFetcher.fetch(getVal("content"), function(data)
      {
        if (data["success"])
          $scope.items = data["items"];
        else if (data["success"] === 0)
          //PANDA, populate sad guy.
          alert(data["message"]);
        else
          alert(data["message"] + "Error: " + data["success"]);
        });
    }
    else //error loading page
      alert("Error loading list, please try again!");
    
  }) //end list controller
    
  //contact controller
  .controller('ContactController', function($scope, $http)
  {
    $scope.post = {}; //post params for http request
    $scope.url = "http://mierze.gear.host/grouple/api/get_contacts.php";
    $scope.post.email = storage.getItem("email");
    //http request to fetch list from server PANDA refactor out this
    $http(
    {
      method  : 'POST',
      url     : $scope.url,
      data    : $scope.post
    }).success(function(data)
    {
      if (data["success"])
        $scope.contacts = data["contacts"];
      else if (data["success"] === 0)
      //PANDA, populate sad guy.
        alert(data["message"]);
      else //generic catch
        alert(data["message"]);
    })
    .error(function(data)
    {
      alert("Error contacting server.");
    });
  }) //end contact controller
  
  //message controller
  .controller('MessageController', function($scope, $http)
  {
    $scope.post = {}; //post params for http request
    $scope.init = function(content)
    { //start init function
      //set url
      if (content === "user")
        $scope.url = "http://mierze.gear.host/grouple/api/get_messages.php";
      else
        $scope.url = "http://mierze.gear.host/grouple/api/get_" + content + "_messages.php";
      //set post params
      if (getVal("id") != null)
        $scope.post.id = getVal("id");
      else if (getVal("email") != null)
      {
        $scope.post.sender = getVal("email");
        $scope.post.receiver = storage.getItem("email");
      }
      else
         $scope.post.email = storage.getItem("email");
      $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : $scope.url,
        data    : $scope.post
      }).success(function(data)
      {
        if (data["success"] === 1)
          $scope.messages = data["messages"];
        else if (data["success"] === 0)
          //PANDA, populate sad guy.
          alert(data["message"]);
        else //generic catch
          alert(data["message"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    } //end init function
    $scope.send = function(message)
    { //start send
      //PANDA put in multiple message types here and handle that
      //PANDA rename all sender to to...
      message.to = $scope.post.sender;
      message.from = $scope.post.receiver;
      $http(
      { //http request to send a message
        method  : 'POST',
        url     : "http://mierze.gear.host/grouple/api/send_message.php",
        data    : message
      }).success(function(data)
      {
        if (data["success"])
          alert("Sent message successfully!");
        else if (data["success"] === 0)
          alert(data["messages"]);
        else //generic catch
          alert(data["message"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    } //end send function
  }) //end message controller
  
  //login controller
  .controller('LoginController', function($scope, $http)
  {
    alert("LOGIN CONTROLLER");
    $scope.user = {};
    //check for stay_logged
    if (storage.getItem("email") != null && storage.getItem("stayLogged"))
    {
      alert("in");
      document.location.href = "home.html"; //bypass login
    }
    //login function
    $scope.login = function()
    {
      $http(
      { //http request to attempt login
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/login.php',
        data    : $scope.user
      }).success(function(data)
      {
        alert("1");
        if (data["success"] === 1)
        { //successful login
          alert(data["message"]);
          //set storage items
          if ($scope.user.stayLogged)
            storage.setItem("stayLogged", true);
          else
            storage.setItem("stayLogged", false);
          storage.setItem("email", $scope.user.email);
          //PANDA: set name here too
          document.location.href = "home.html";
        }
        else //generic catch
          alert(data["message"] + " Error: " + data["success"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end login function
  }) //end login controller
  
  //register controller
  .controller('RegisterController', function($scope, $http)
  {
    $scope.user = {};
    //register function
    $scope.register = function()
    {
      $http(
      { //http request to register account
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/register.php',
        data    : $scope.user
      }).success(function(data)
      {
        if (data["success"])
          //successful register
          alert(data["message"]);
        else //generic catch
          alert("Error registering account.");
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end register function
  }) //end register controller
  
  //add friend controller
  .controller('AddFriendController', function($scope, $http)
  {
    $scope.invite = {};
    $scope.invite.sender = storage.getItem("email");
    //send invite function
    $scope.sendInvite = function()
    { //start send invite function
      $http(
      { //http request to add friend
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/add_friend.php',
        data    : $scope.invite
      }).success(function(data)
      {
        if (data["success"] === 1)
        {
          //successful friend added
          alert(data["message"]);
        }
        else
        {
          //generic catch
          alert("Error fetching profile.\n"+data["message"]);
        }
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end send invite function
  }) //end add friend controller

  //event create controller
  .controller('EventCreateController', function($scope, $http, $filter)
  {
    $scope.info = {};
    $scope.info.recurring = 0;
    $scope.info.creator = storage.getItem("email");
    $scope.create = function()
    { //start create function
      //check all inputs are valid
      if ($scope.info.minPart == null)
        $scope.info.minPart = 1;
      if ($scope.info.maxPart == null)
        $scope.info.maxPart = 0;
      if ($scope.info.recType) {
        //code
      }
      $scope.info.startDate = $filter('date')($scope.info.startDate, "yyyy-MM-dd hh:mm:ss");
      $scope.info.endDate = $filter('date')($scope.info.endDate, "yyyy-MM-dd hh:mm:ss");
      $http(
      { //http request to create event
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/create_event.php',
        data    : $scope.info
      }).success(function(data)
      {
        if (data["success"])
          //successful event create
          alert(data["message"]);
        else
          //generic catch
          alert("Success: " + data["success"] + "\nError creating event.\n" + data["message"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end create function
  }) //end create event controller
  
  //group create controller
  .controller('GroupCreateController', function($scope, $http, $filter)
  {
    $scope.info = {};
    $scope.info.creator = storage.getItem("email");
    $scope.create = function()
    { //start create function
      //check all inputs are valid
      $http(
      { //http request to create group
        method  : 'POST',
        url     : 'http://mierze.gear.host/grouple/api/create_group.php',
        data    : $scope.info
      }).success(function(data)
      {
        if (data["success"])
          //successful group create
          alert(data["message"]);
        else
          //generic catch
          alert(data["message"] + "Error: " + data["success"]);
      })
      .error(function(data)
      {
        alert("Error contacting server.");
      });
    }; //end create function
  }); //end create group controller
})();