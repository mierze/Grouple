'use strict'
function ContactController($rootScope, MessageFetcher) {
  var vm = this;
  var storage = window.localStorage;
  var params = {}; //params for http request
  params.email = storage.getItem('email');
  $rootScope.$broadcast('setTitle', 'Contacts');
  MessageFetcher.fetch(params, 'contacts', function MFcb(data) {
    if (data['success'] === 1)
      vm.contacts = data['data'];
    else
    {
      vm.sadGuy = true;
      alert(data['message']);
    }
  });
}; //end contact controller

module.exports = ContactController;