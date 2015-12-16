'use strict'
function EntityMessageController($rootScope, $stateParams, MessageFetcher, MessageSender) {
  //entity message controller
  var vm = this;
  var storage = window.localStorage;
  var type = $stateParams.content;
  vm.post = {}; //post params for http request
  vm.init = init;
  vm.send = send;
  $rootScope.$broadcast('setTitle', 'Messages');
  //functions
  function init() {
    //init function
    vm.post.id = $stateParams.id;
    vm.post.user = storage.getItem('email');
    vm.post.from = storage.getItem('email');
    MessageFetcher.fetch(vm.post, type, function(data) {
      if (data['success'])
        vm.messages = data['data'];
      else
        //PANDA, populate sad guy.
        alert(data['message']);
    });
  }; //end init function
  function send() {
    //start send
    MessageSender.send(vm.post, type, function(data) {
      alert(data['message']);
    });
  }; //end send function
}; //end entity message controller

module.exports = EntityMessageController;

