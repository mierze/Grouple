'use strict'
module.exports = function($http)
{ //event inviter takes in a to and from and sends the invite
  var send = function(post, callback)
  { //send function
    //TODO: should take in a list of gids
      //will need to tweak the php to take in a list
    alert("Post is\n" + JSON.stringify(post));
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : 'http://mierze.gear.host/grouple/api/invite_groups.php',
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
