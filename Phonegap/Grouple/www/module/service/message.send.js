(function() //wrap
{
  //session storage
  var storage = window.localStorage;
  var MessageSender = function($http)
  { //MessageSender sends user, group and event messages
    var send = function(post, type, callback)
    { //start send
      alert("type is " + type);
      if (type === 'user')
        this.url = 'http://mierze.gear.host/grouple/api/send_message.php';
      else
        this.url = 'http://mierze.gear.host/grouple/api/send_' + type + '_message.php';
      $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : this.url,
        data    : post
       }).then(
      function(result) {
        return callback(result.data);
      });
    }; //end send
    return {
      send: send
    };
  }; //end MessageSender
  angular.module('service').factory('MessageSender', MessageSender);
})(); //end wrap