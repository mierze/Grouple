'use strict'
module.exports = function($rootScope, FriendInviter)
{ //friends controller
  var vm = this,
  storage = window.localStorage;
  $rootScope.$broadcast('setTitle', 'Events');
  vm.post = {};
  vm.showAddFriend = false;
  vm.post.from = storage.getItem('email');
  vm.send = send;
  vm.toggleAddFriend = toggleAddFriend;
  
  //functions
  function send()
  {
    FriendInviter.send(vm.post, function(data)
    {
      alert(data['message']);
    });
  };
  function toggleAddFriend()
  {
    if (vm.showAddFriend) 
      vm.showAddFriend = false;
    else
      vm.showAddFriend = true;
  };
}; //end friends controller