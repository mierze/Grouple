'use strict'
module.exports = function($rootScope, $stateParams, ListFetcher)
{ //badge list controller
  var vm = this,
  storage = window.localStorage,
  type = 'badges',
  title = 'Badges',
  params = {};
  $rootScope.$broadcast('setTitle', title);

  if ($stateParams.id == null || ($stateParams.id.length < 2))
    params.id = storage.getItem('email');
  else
    params.id = $stateParams.id;  
  params.user = storage.getItem('email');

  fetchList();
  
  //functions
  function fetchList()
  {
    ListFetcher.fetch(params, type, function(data)
    { //start fetch list
      if (data['success'] === 1)
        vm.items = data['items'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end fetch list
  };
}; //end badge list controller
