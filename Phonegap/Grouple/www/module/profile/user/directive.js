'use strict'
module.exports = function($filter, ProfileEditer)
{ //event edit directive
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: "module/profile/user/user-edit.html",
    controller: function()
    {
      this.save = function(info)
      {
        info.gender = 'm';
        
        //this.info.email = info.email;
        alert(JSON.stringify(info));

        //http request to fetch list from server PANDA refactor out this
        ProfileEditer.edit(info, 'user', function(data)
        {            
          alert(data["message"]);
        });    
      };
    },
    controllerAs: "userEdit"
  };
}; //end edit user profile directive
