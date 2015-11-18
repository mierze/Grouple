'use strict'
module.exports = function(FriendInviter)
{ //wrap
  var vm = this;
  var storage = window.localStorage;
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
}; //end wrap