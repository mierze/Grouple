'use strict'
function UserMessageController($rootScope, $stateParams, $state, UserMessageGetter, UserMessageSender) {
  var vm = this;
  var storage = window.localStorage;
  vm.send = send;
  vm.init = init;
  
  //functions
  function init() {
    //alert('made it');
    vm.email = storage.getItem('email');
    vm.contact = $stateParams.contact;
    alert('made ' + JSON.stringify($stateParams));
    $rootScope.$broadcast('setTitle', 'Messages');
    getMessages();
  } //end init function
  function getMessages() {
    vm.params = {};
    vm.params.contact = vm.contact;
    vm.params.email = vm.email;
    UserMessageGetter.get(vm.params, function setMessages(data) {
      if (data['success'] === 1)
        vm.messages = data['data'];
      else {
        alert(data['message']);
        vm.sadGuy = true;
      }
    });
  }
  function send() {
    vm.post = {};
    vm.post.contact = contact;
    vm.post.email = storage.getItem('email');
    UserMessageSender.send(post, function sendMessage(data) {
        $state.go('user-messages', {id: vm.contact}, {reload: true});      
    });
  } //end send function
} //end user message controller

module.exports = UserMessageController;
