(function()
{ //wrap
  var storage = window.localStorage;
  //create module controllers
  angular.module('list')
    
  /*********************************************
  ************* CONTROLLERS BELOW **************
  *********************************************/
  //list controller, serves as the controller for all list pages
  .controller('ListController', function($scope, ListFetcher, $stateParams)
  {
    alert("LIST CONTROLLER");
    if ($stateParams.content != null)
    { //ensure content is set
      if ($stateParams.content === "friend_invites")
      { //editable check
        $scope.editable = true;
      }
      //fetch data and wait for callback
      ListFetcher.fetch($stateParams.content, function(data)
      {
        alert(data["success"]);
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
    
  }); //end list controller
})();