'use strict'
module.exports = function($filter, ProfileEditer, $state)
{ //event edit directive
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/user/user-edit.html',
    controller: function()
    {
      this.save = function(info)
      {
        var type = 'user';
        //formatting date
        var year = info.birthday.getUTCFullYear();
        var month = info.birthday.getUTCMonth() + 1;
        var day = info.birthday.getUTCDay()+1;
        var birthday =  year + '-' + month + '-' + day;
        info.birthday = birthday;
        //TODO: figure gender out!!!
       // info.gender === 'Male' ? info.gender = 'm' : info.gender = 'f';
        //ensure all info set
        alert('Before editer service.\n' + JSON.stringify(info));
        //info.gender = 'm';
        //http request to fetch list from server PANDA refactor out this
        ProfileEditer.edit(info, type, function(data)
        {            
          alert(data['message']);
          //if successful update ui and close out
          if (data["success"] === 1)
          {
            $state.go($state.current, {id: type}, {reload: true})
          }
        });    
      };
      this.showErrors = function()
      {
        alert('Error in edit form, please try again!');
      };
    },
    controllerAs: 'userEdit'
  };
}; //end edit user profile directive
