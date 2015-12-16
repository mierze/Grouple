'use strict'
function GroupInviteController($stateParams, /*FriendInviter, GROUPINVITER*/ ListFetcher, GroupInviter) {
  //group invite controller
  var vm = this;
  var storage = window.localStorage;
  vm.invites = {};
  vm.items = {};
  vm.init = init;
  vm.toggleRole = toggleRole;
  vm.send = send;
  
  //functions
  function init() {
    //TODO: should also grab group members and remove those from the friends list and then display that
    var post = {};
    post.id = storage.getItem("email");
    post.user = storage.getItem("email");
    ListFetcher.fetch(post, /*type of content to grab*/'friends', function(data) {
      //start fetch list of groups to invite
      if (data["success"] === 1) {
        alert(JSON.stringify(data));
        vm.items = data["items"];
      }
      else
        alert(data["message"]);
    }); //end fetch list
  };
  function toggleRole(id, role) {
    if (role === 'M')
      role = 'A'
    else if (role === 'A')
      role = '-';
    else
      role = 'M';
    
    //add role under invites unless role is '-'
    if (role !== '-')
      vm.invites[id] = role;
    else
     delete vm.invites[id];
    return role;
  };
  function send() {
    var post = {};
    alert(JSON.stringify(vm.invites));
    post.id = $stateParams.id;
    post.invites = vm.invites;
    GroupInviter.send(post, function(data)
    {
      //TODO: allow change invite mod?
      alert(JSON.stringify(data));//data["message"]);
    });
  };
}; //end group invite controller

module.exports = GroupInviteController;