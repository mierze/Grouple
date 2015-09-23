(function() //wrap
{
  //session storage
  var storage = window.localStorage;
  var FriendInviter = function($http)
  { //ListFetcher is a service for fetching any type of list in grouple
    var send = function(post, callback)
    {
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
    };
    return {
      send: send
    };
  };
  angular.module('service').factory('FriendInviter', FriendInviter);
})(); //end wrap
