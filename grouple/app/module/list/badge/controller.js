'use strict'
function BadgeListController($rootScope, $stateParams, ListFetcher) {
  //badge list controller
  var vm = this;
  var storage = window.localStorage;
  var type = 'badges';
  var title = 'Badges';
  var params = {};
  $rootScope.$broadcast('setTitle', title);

  if ($stateParams.id == null || ($stateParams.id.length < 2))
    params.id = storage.getItem('email');
  else
    params.id = $stateParams.id;  
  params.user = storage.getItem('email');

  fetchList();
  
  //functions
  function fetchList() {
    ListFetcher.fetch(params, type, function(data) {
      //start fetch list
      if (data['success'] === 1)
        vm.items = data['data'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end fetch list
  };
}; //end badge list controller

module.exports = BadgeListController;