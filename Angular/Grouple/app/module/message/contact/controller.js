'use strict'
module.exports = function(MessageFetcher)
{ //contact controller
  var vm = this;
  var storage = window.localStorage;
  vm.post = {}; //post params for http request
  vm.post.email = storage.getItem('email');
  vm.post.user = storage.getItem('email');
  MessageFetcher.fetch(vm.post, 'contacts', function(data)
  {
    if (data['success'])
      vm.contacts = data['contacts'];
    else
      //PANDA, populate sad guy.
      alert(data['message']);
  });
}; //end contact controller