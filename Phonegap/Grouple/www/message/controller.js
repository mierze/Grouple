(function()
{ //wrap
  var storage = window.localStorage;
  //create module controllers
  angular.module('message')
    
  /*********************************************
  ************* CONTROLLERS BELOW **************
  *********************************************/

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
  .controller('MessageController', function($scope, $http, $stateParams)
  {
    $scope.post = {}; //post params for http request
    $scope.init = function(content)
    { //start init function
      //set url
      if (content === "user")
      {
        $scope.url = "http://mierze.gear.host/grouple/api/get_messages.php";
        $scope.post.from = $stateParams.id;
        $scope.post.to = storage.getItem("email");
      }
      else
      { //group / event message
        $scope.url = "http://mierze.gear.host/grouple/api/get_" + content + "_messages.php";
        //set post params
        if ($stateParams.id != null && $stateParams.id.length > 2)
          $scope.post.id = $stateParams.id;
        else
          alert("ERROR");
      }
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
  }); //end message controller
})();