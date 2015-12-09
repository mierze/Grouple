'use strict'
module.exports = function($rootScope, $stateParams, ListFetcher)
{ //user list controller
  var vm = this,
  storage = window.localStorage,
  params = {};
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
  function setTitle(content)
  {
    var title;
    if ($stateParams.content === 'friend_invites')
    { //editable check
      vm.invite = true;
      title = 'Friend Invites';
    }
    else if ($stateParams.content === 'friends')
      title = 'Friends';
    else if ($stateParams.content === 'group_members')
      title = 'Group Members';
    else
      title = 'Users';
    $rootScope.$broadcast('setTitle', title); 
  };
  function fetchList()
  {
    ListFetcher.fetch(params, $stateParams.content, function(data)
    { //start fetch list
      if (data['success'] === 1)
      {
        vm.items = data['items'];
        vm.mod = data['mod'];
        alert("MOD IS : " + vm.mod);
      }
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end fetch list
  };
}; //end user list controller
