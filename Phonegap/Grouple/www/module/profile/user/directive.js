'use strict'
module.exports = function($filter, ProfileEditer)
{ //event edit directive
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/user/user-edit.html',
    controller: function()
    {
      this.save = function(info)
      {
        info.birthday = info.birthday.getUTCFullYear() + '-' + info.birthday.getUTCMonth() + '-' + info.birthday.getUTCDate();
        //TODO: figure gender out!!!
       // info.gender === 'Male' ? info.gender = 'm' : info.gender = 'f';
        //ensure all info set
        alert('Before editer service.\n' + JSON.stringify(info));
        //info.gender = 'm';
        //http request to fetch list from server PANDA refactor out this
        ProfileEditer.edit(info, 'user', function(data)
        {            
          alert(data['message']);
          //if successful update ui and close out
        });    
      };
      this.showErrors = function()
      {
        alert('Error in edit for, please try again!');
      };
    },
    controllerAs: 'userEdit'
  };
}; //end edit user profile directive
