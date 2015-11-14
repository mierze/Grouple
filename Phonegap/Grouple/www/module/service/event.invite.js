'use strict'
module.exports = function($http)
{ //event inviter takes in a to and from and sends the invite
  var storage = window.localStorage;
  var send = function(post, callback)
  { //send function
    //TODO: should take in a list of gids
      //will need to tweak the php to take in a list
    //could merge this service as inviter.js
    alert("Post is\n" + JSON.stringify(post));
    post.from = storage.getItem('email');
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : 'http://mierze.gear.host/grouple/api/invite_to_event.php',
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end send function
  return {
    send: send
  };
}; //end event inviter
