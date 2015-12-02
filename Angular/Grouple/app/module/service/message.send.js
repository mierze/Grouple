'use strict'
module.exports = function($http)
{ //message sender sends user, group and event messages
  var send = function(post, type, callback)
  { //send function
    if (type === 'user')
      var url = 'http://grouple.gear.host/api/send_message.php';
    else
      var url = 'http://grouple.gear.host/api/send_' + type + '_message.php';
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : url,
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end send function
  return {
    send: send
  };
}; //end message sender
