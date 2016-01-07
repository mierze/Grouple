'use strict'
function EntityMessageController($rootScope, $stateParams, EnitityMessageGetter, EntityMessageSender) {
  var vm = this;
  var storage = window.localStorage;
  vm.init = init;
  vm.send = send;
  
  //functions
  function init() {
    vm.type = $stateParams.content;
    vm.id = $stateParams.id;
    vm.email = storage.getItem('email');
    $rootScope.$broadcast('setTitle', 'Messages');
    getMessages();
  } //end init function
  function getMessages() {
    EntityMessageGetter.get(vm.id, vm.type, function setMessages(data) {
      if (data['success'])
        vm.messages = data['data'];
      else {
        alert(data['message']);
        vm.sadGuy = true;
      }
    });
  } //end get messages
  function send() {
    vm.post = {};
    vm.post.id = vm.id;
    vm.post.from = vm.email;
    EntityMessageSender.send(vm.post, vm.type, function sendMessage(data) {
      alert(data['message']);
    });
  } //end send function
} //end entity message controller

module.exports = EntityMessageController;

