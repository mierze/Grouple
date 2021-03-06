'use strict'
function UserEditDirective($filter, UserEditer, $state) {
  return {
    restrict: 'E',
    templateUrl: 'module/profile/user/part/user-edit.html',
    controller: userEditCtrl,
    controllerAs: 'userEditCtrl'
  };

  function userEditCtrl() {
    var vm = this;
    var storage = window.localStorage; //grab local storage
    vm.type = 'user';
    vm.save = save;
    vm.showErrors = showErrors;
    //functions
    function save(info) {
      //formatting date
      var birthday =  info.year + '-' + info.month + '-' + info.day;
      info.birthday = birthday;
      UserEditer.edit(info, function(data) {
        //if successful update ui and close out
        //if (data['success'] == 1) {
          $state.go($state.current, {id: vm.type}, {reload: true})
        //}
      });
    } //end save
    function showErrors() {
      alert('Error in edit form, please try again!');
    }
  } //end user edit controller
} //end user edit directive

module.exports = UserEditDirective;
