'use strict'
function UserEditDirective($filter, UserEditer, $state) {
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/user/part/user-edit.html',
    controller: function() {
      var vm = this;
      vm.type = 'user';
      vm.save = save;
      vm.showErrors = showErrors;
      
      //functions
      function save(info) {
        //formatting date
        var year = info.birthday.getUTCFullYear();
        var month = info.birthday.getUTCMonth() + 1;
        var day = info.birthday.getUTCDay() + 1;
        var birthday =  year + '-' + month + '-' + day;
        info.birthday = birthday;
        //TODO: figure gender out!!!
       // info.gender === 'Male' ? info.gender = 'm' : info.gender = 'f';
        //ensure all info set
        alert('Before editer service.\n' + JSON.stringify(info));
        UserEditer.post(info, function(data) {            
          alert(data['message']);
          //if successful update ui and close out
          if (data["success"] === 1) {
            $state.go($state.current, {id: type}, {reload: true})
          }
        });    
      } //end save
      function showErrors() {
        alert('Error in edit form, please try again!');
      }
    },
    controllerAs: 'userEditCtrl'
  };
} //end user edit directive

module.exports = UserEditDirective;
