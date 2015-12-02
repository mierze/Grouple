'use strict'
module.exports = function($rootScope, MessageFetcher)
{ //contact controller
  var vm = this,
  storage = window.localStorage;
  vm.post = {}; //post params for http request
  vm.post.email = storage.getItem('email');
  vm.post.user = storage.getItem('email');
  $rootScope.$broadcast('setTitle', 'Contacts');
  MessageFetcher.fetch(vm.post, 'contacts', function(data)
  {
    if (data['success'])
      vm.contacts = data['contacts'];
    else
      //PANDA, populate sad guy.
      alert(data['message']);
  });
}; //end contact controller