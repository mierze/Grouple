'use strict'
function BadgeListController($rootScope, $stateParams, BadgeGetter) {
  var vm = this;
  var storage = window.localStorage;
  $rootScope.$broadcast('setTitle', 'Badges');

  if ($stateParams.email == null || ($stateParams.email.length < 2))
    vm.email = storage.getItem('email');
  else
    vm.email = $stateParams.email;

  getBadges();
  
  //functions
  function getBadges() {
    BadgeGetter.get(vm.email, function(data) {
      if (data['success'] === 1)
        vm.items = data['data'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    });
  } //end get badges
} //end badge list controller

module.exports = BadgeListController;