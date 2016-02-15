'use strict'
function ContactController($rootScope, ContactGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.email = storage.getItem('email');
  $rootScope.$broadcast('setTitle', 'Contacts');
  ContactGetter.get(vm.email, function setContacts(data) {
    if (data['success'] === 1)
      vm.contacts = data['data'];
    else {
      vm.sadGuy = true;
      alert(data['message']);
    }
  });
} //end contact controller

module.exports = ContactController;