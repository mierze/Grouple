'use strict'
module.exports = function($stateParams, EventInviter, ListFetcher)
{ //event invite controller
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.post.id = storage.getItem('email');
  vm.post.user = storage.getItem('email');
  vm.invites = [];
  vm.init = init;
  vm.toggleRow = toggleRow;
  vm.send = send;
  
  //functions
  function init()
  {
    ListFetcher.fetch(vm.post, /*type of content to grab*/'groups', function(data)
    { //start fetch list of groups to invite
      if (data['success'] === 1)
        vm.items = data['items'];
      else
        alert(data['message']);
    }); //end fetch list
  };
  function toggleRow(id)
  {
    alert("id is " + id);
    if ((vm.invites).indexOf(id) !== null && (vm.invites).indexOf(id) >= 0)
    {
      alert((vm.invites).indexOf(id));
      (vm.invites).splice(vm.invites.indexOf(id), 1);
    }
    else
    {
      alert((vm.invites).indexOf(id));
      (vm.invites).push(id);
    }
    alert(JSON.stringify(vm.invites));
  };
  function send()
  {
    var post = {};
    post.from = storage.getItem("email");
    post.id = $stateParams.id;
    post.invites = vm.invites;
    alert(JSON.stringify(post));
    EventInviter.send(post, function(data)
    {
      alert(JSON.stringify(data));//data["message"]);
    });
  };
}; //end event invite controller