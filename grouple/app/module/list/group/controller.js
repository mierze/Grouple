'use strict'
module.exports = function($rootScope, $stateParams, ListFetcher)
{ //group list controller
  var vm = this,
  storage = window.localStorage,
  params = {};

  setTitle($stateParams.content);
  if ($stateParams.id == null)
    params.id = storage.getItem('email');
  else
    params.id = $stateParams.id;
  params.user = storage.getItem('email');
  
  fetchList();
  
  //functions
  function setTitle(content)
  {
    var title;
    if ($stateParams.content === 'group_invites')
    { //editable check
      vm.invite = true;
      title = 'Group Invites';
    }
    else if (content === 'groups_attending')
      title = 'Groups Attending';
    else
      title = 'Groups';
    $rootScope.$broadcast('setTitle', title);
  };
  function fetchList()
  {
    ListFetcher.fetch(params, $stateParams.content, function(data)
    { //start fetch list
      if (data['success'] === 1)
        vm.items = data['items'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end fetch list
  };
}; //end group list controller
