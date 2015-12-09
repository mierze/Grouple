'use strict'
module.exports = function($http)
{ //message sender sends user, group and event messages
  var send = function(post, type, callback)
  { //send function
    var url = 'https://groupleapp.herokuapp.com/' + type + '/messages/send';
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
