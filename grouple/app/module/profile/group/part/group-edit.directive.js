'use strict'
function GroupEditDirective($state, GroupEditer) {
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/group/part/group-edit.html',
    controller: groupEditCtrl,
    controllerAs: 'groupEditCtrl'
  };
  
  function groupEditCtrl() {
    var vm = this;
    var type = 'group';
    vm.save = save;
    vm.showErrors = showErrors;
    
    //functions
    function save(info) {
      alert('Before editer service.\n' + JSON.stringify(info));
      GroupEditer.edit(info, type, function(data) {            
        alert(data['message']);
        //if successful update ui and close out
        if (data["success"] === 1) {
          $state.go($state.current, {id: info.id}, {reload: true})
        }
      });    
    }
    function showErrors() {
      alert('Error in edit form, please try again!');
    };
  } //end group edit controller
} //end group edit directive

module.exports = GroupEditDirective;