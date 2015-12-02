'use strict'
module.exports = function($state, $http, ProfileEditer)
{
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/group/part/group-edit.html',
    controller: function()
    {
      var vm = this,
      type = 'group';
      vm.save = save;
      vm.showErrors = showErrors;
      
      //functions
      function save(info)
      {
        alert('Before editer service.\n' + JSON.stringify(info));
        ProfileEditer.edit(info, type, function(data)
        {            
          alert(data['message']);
          //if successful update ui and close out
          if (data["success"] === 1)
          {
            $state.go($state.current, {id: info.id}, {reload: true})
          }
        });    
      };
      function showErrors()
      {
        alert('Error in edit form, please try again!');
      };
    },
    controllerAs: 'groupEditCtrl'
  };
}; //end edit group profile directive