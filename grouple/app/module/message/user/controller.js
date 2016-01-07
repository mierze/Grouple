'use strict'
function UserMessageController($rootScope, $stateParams, $state, $interval, UserMessageGetter, UserMessageSender) {
  var vm = this;
  var storage = window.localStorage;
  vm.send = send;

  //functions
  function init() {
    vm.email = storage.getItem('email');
    vm.contact = $stateParams.id;
    $rootScope.$broadcast('setTitle', 'Messages');
    setMessages();
  } //end init function
  function setMessages() {
    vm.params = {};
    vm.params = {}; //post params for http request
    vm.params.contact = contact;
    vm.params.id = email;
    UserMessageGetter.get(params, function setMessages(data) {
      if (data['success'] === 1)
        vm.messages = data['data'];
      else {
        alert(data['message']);
        vm.sadGuy = true;
      }
    });
  }
  function send() { //start send
    var post = {};
    post.contact = contact;
    post.email = storage.getItem('email');
    UserMessageSender.send(post, function sendMessage(data) {
        $state.go('user-messages', {id: contact}, {reload: true});      
    });
  } //end send function
} //end user message controller

module.exports = UserMessageController;
