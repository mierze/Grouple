'use strict'
module.exports = function($http)
{
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: "module/profile/group/group-edit.html",
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
}; //end edit group profile directive