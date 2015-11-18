'use strict'
module.exports = function($stateParams, ListFetcher)
{ //list controller
  var storage = window.localStorage;
  var vm = this;
  if ($stateParams.content != null)
  { //ensure content is set
    if ($stateParams.content === 'friend_invites' || $stateParams.content === 'group_invites')
    { //editable check
      vm.invite = true;
    }
    //prepare post parameters
    vm.post = {};
    //TODO switch content to type
    if ($stateParams.id == null || ($stateParams.id.length < 2 && ($stateParams.content === 'user' || $stateParams.content === 'badges')))
      vm.post.id = storage.getItem('email');
    else
      vm.post.id = $stateParams.id;
    vm.post.user = storage.getItem('email');
    ListFetcher.fetch(vm.post, $stateParams.content, function(data)
    { //start fetch list
      if (data['success'])
      {
       // alert(JSON.stringify(data['items']));
        vm.items = data['items'];
      }
      else
        //TODO, populate sad guy.
        alert(data['message']);
    }); //end fetch list
  }
  else //error loading page
    alert('Error loading list, please try again!');  
}; //end list controller
