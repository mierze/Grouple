'use strict'
function EventInviteController($stateParams, EventInviter, GroupListGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.email = storage.getItem('email');
  vm.id = $stateParams.id;
  vm.invites = [];
  vm.init = init;
  vm.toggleRow = toggleRow;
  vm.send = send;
  
  //functions
  function init() {
    GroupListGetter.get(vm.email, /*type of content to grab*/'groups', function setGroups(data) {
      if (data['success'] === 1)
        vm.items = data['items'];
      else
        alert(data['message']);
    });
  } //end init
  function toggleRow(id) {
    alert("id is " + id);
    if ((vm.invites).indexOf(id) !== null && (vm.invites).indexOf(id) >= 0) {
      alert((vm.invites).indexOf(id));
      (vm.invites).splice(vm.invites.indexOf(id), 1);
    }
    else {
      alert((vm.invites).indexOf(id));
      (vm.invites).push(id);
    }
    alert(JSON.stringify(vm.invites));
  }
  function send() {
    var post = {};
    post.from = vm.email;
    post.id = $stateParams.id;
    post.invites = vm.invites;
    alert(JSON.stringify(post));
    EventInviter.send(post, function(data)
    {
      alert(JSON.stringify(data));//data["message"]);
    });
  }
} //end event invite controller

module.exports = EventInviteController;