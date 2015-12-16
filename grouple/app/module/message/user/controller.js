'use strict'
function UserMessageController($rootScope, $stateParams, $state, $interval, MessageFetcher, MessageSender) {
  var vm = this;
  var type = 'user';
  var storage = window.localStorage;
  var contact = $stateParams.id;
  vm.init = init;
  vm.send = send;
  $rootScope.$broadcast('setTitle', 'Messages');
  
  //functions
  function init() {
    //init function
    var params = {}; //post params for http request
    params.contact = contact;
    params.email = storage.getItem('email');
    //before we implement the fetch being triggered by new msgs, lets use an interval
    /*$interval(*/MessageFetcher.fetch(params, type, function MFcb(data) {
      if (data['success'] === 1)
        vm.messages = data['data'];
      else
      {
        alert(data['message']);
        vm.sadGuy = true;
      }
    });/*, 5000);*/
  }; //end init function
  function send() { //start send
    var post = {};
    post.contact = contact;
    post.email = storage.getItem('email');
    MessageSender.send(post, type, function MScb(data)
    {
        $state.go('user-messages', {id: contact}, {reload: true});      
    });
  }; //end send function
}; //end user message controller

module.exports = UserMessageController;
