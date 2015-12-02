'use strict'
module.exports = function($rootScope, $stateParams, $state, $interval, MessageFetcher, MessageSender)
{ //user message controller
  var vm = this;
  var type = 'user';
  var storage = window.localStorage;
  vm.post = {}; //post params for http request
  vm.init = init;
  vm.send = send;
  $rootScope.$broadcast('setTitle', 'Messages');
  //functions
  function init()
  { //init function
    vm.post.contact = $stateParams.id;
    vm.post.user = storage.getItem('email');
    //before we implement the fetch being triggered by new msgs, lets use an interval
    /*$interval(*/MessageFetcher.fetch(vm.post, type, function(data)
    {
      if (data['success'] === 1)
        vm.messages = data['messages'];
      else
        //TODO, populate sad guy.
        alert(data['message']);
    });/*, 5000);*/
  }; //end init function
  function send()
  { //start send
    MessageSender.send(vm.post, type, function(data)
    {
        $state.go('user-messages', {id: vm.post.contact}, {reload: true});      
    });
  }; //end send function
}; //end user message controller
