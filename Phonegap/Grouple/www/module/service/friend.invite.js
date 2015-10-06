'use strict'
module.exports = function($http)
{ //friend inviter takes in a to and from and sends the invite
  var send = function(post, callback)
  { //send function
    alert(JSON.stringify(post));
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : 'http://mierze.gear.host/grouple/api/invite_friend.php',
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end send function
  return {
    send: send
  };
}; //end friend inviter
