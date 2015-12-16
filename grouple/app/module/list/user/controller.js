'use strict'
function UserListController($rootScope, $stateParams, ListFetcher) {
  //user list controller
  var vm = this;
  var storage = window.localStorage;
  var params = {};
  
  vm.setTitle = setTitle;
  vm.fetchList = fetchList;
  
  setTitle($stateParams.content);
  
  if ($stateParams.id == null || $stateParams.id === 'user')
    params.id = storage.getItem('email');
  else
    params.id = $stateParams.id;
  //params.user = storage.getItem('email');
  
  fetchList();
  
  //functions
  function setTitle(content) {
    var title;
    if ($stateParams.content === 'friend-invites') {
      //editable check
      vm.invite = true;
      title = 'Friend Invites';
    }
    else if ($stateParams.content === 'friends')
      title = 'Friends';
    else if ($stateParams.content === 'group-members')
      title = 'Group Members';
    else
      title = 'Users';
    $rootScope.$broadcast('setTitle', title); 
  };
  function fetchList() {
    ListFetcher.fetch(params, $stateParams.content, function(results) {
      //start fetch list
      if (results['success'] === 1) {
        vm.items = results['data'];
        vm.mod = results['mod'];
        alert("MOD IS : " + vm.mod);
      }
      else if (results['success'] === 0)
        vm.sadGuy = true;
      else
        alert(results['message']);
    }); //end fetch list
  };
}; //end user list controller

module.exports = UserListController;


