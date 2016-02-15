'use strict'
function GroupInviteController($stateParams, UserListGetter, GroupInviter) {
  var vm = this;
  var storage = window.localStorage;
  vm.invites = {};
  vm.items = {};
  vm.init = init;
  vm.toggleRole = toggleRole;
  vm.send = send;
  
  //functions
  function init() {
    vm.email = storage.getItem("email");
    vm.id = $stateParams.id;
    getUsers();
  } //end init
  function getUsers() {
    UserListGetter.get(vm.email, 'friends', function setUsers(data) {
      //start fetch list of groups to invite
      if (data["success"] === 1) {
        alert(JSON.stringify(data));
        vm.items = data["items"];
      }
      else
        alert(data["message"]);
    });
  }
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
  } //end toggle role
  function send() {
    var post = {};
    alert(JSON.stringify(vm.invites));
    post.id = vm.id;
    post.invites = vm.invites;
    GroupInviter.send(post, function inviteResult(data)
    {
      //TODO: allow change invite mod?
      alert(JSON.stringify(data));//data["message"]);
    });
  } //end send
} //end group invite controller

module.exports = GroupInviteController;