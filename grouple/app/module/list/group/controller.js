'use strict'
function GroupListController($rootScope, $stateParams, GroupListGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.setTitle = setTitle;
  vm.getGroups = getGroups;
  vm.content = $stateParams.content;
  if ($stateParams.id == null)
    vm.email = storage.getItem('email');
  else
    vm.email = $stateParams.id;
    
  //initialize
  vm.getGroups();
  vm.setTitle();

  //functions
  function setTitle(content) {
    if (vm.content === 'invites') {
      //editable check
      vm.invite = true;
      vm.title = 'Group Invites';
    }
    else if (vm.content === 'groups-attending')
      vm.title = 'Groups Attending';
    else
      vm.title = 'Groups';
    $rootScope.$broadcast('setTitle', vm.title);
  }
  function getGroups() {
    alert('now in get groups');
    GroupListGetter.get(vm.email, vm.content, function setGroups(data) {
      if (data['success'] === 1)
        vm.items = data['data'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end get groups
  }
}; //end group list controller

module.exports = GroupListController;
