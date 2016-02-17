'use strict'
function UserListController($rootScope, $stateParams, UserListGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.content = $stateParams.content;

  //id for generic
  if ($stateParams.id == null || $stateParams.id === 'user')
    vm.id = storage.getItem('email');
  else
    vm.id = $stateParams.id;

  vm.setTitle = setTitle;
  vm.getUsers = getUsers;

  vm.setTitle();
  vm.getUsers();

  //functions
  function setTitle() {
    if (vm.content === 'friend-invites') {
      vm.invite = true;
      vm.title = 'Friend Invites';
    }
    else if (vm.content === 'friends')
      vm.title = 'Friends';
    else if (vm.content === 'group-members')
      vm.title = 'Group Members';
    else if (vm.content === 'event-participants')
      vm.title = 'Event Participants';
    else
      vm.title = 'Users';
    $rootScope.$broadcast('setTitle', vm.title);
  }
  function getUsers() {
    alert('now in get users ' + vm.content + ' ' + vm.id) ;
    UserListGetter.get(vm.id, vm.content, function setUsers(results) {
      if (results['success'] === 1) {
        vm.items = results['data'];
        alert(JSON.stringify(vm.items));
        vm.mod = results['mod'];
        alert("MOD IS : " + vm.mod);
      }
      else if (results['success'] === 0)
        vm.sadGuy = true;
      else
        alert(results['message']);
    });
  } //end get users
} //end user list controller

module.exports = UserListController;
