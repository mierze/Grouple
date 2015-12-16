'use strict'
module.exports = function($http)
{ //list fetcher is a service for fetching any type of list in grouple
  var fetch = function(params, type, callback) { //start fetch
    var url = 'https://groupleapp.herokuapp.com/api';
    switch(type) {
      case 'friends':
        url += '/user/list/friends';
        break;
      case 'friend-invites':
        url += '/user/list/invites';
        break;
      case 'groups':
        url += '/group/list';
        break;
      case 'invites':
        url += '/group/list/invites';
        break;
      case 'group-members':
        url += '/user/list/members';
        break;
      case 'event-attending':
        url += '/user/list/attending';
        break;
      case 'invites':
        url += '/event/list/invites';
        break;
      case 'upcoming':
        url += '/event/list/upcoming';
        break;
      case 'pending':
      url += '/event/list/pending';
        break;
      case 'past':
        url += '/event/list/past';
        break;
      case 'declined':
        url += '/event/list/declined';
        break;
      case 'badges':
        url += '/user/profile/badges';
        break;
    }
    //attach get route params
    url += '/' + params.id;
    $http(
    {
      method  : 'GET',
      url     : url
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end fetch
  return {
    fetch: fetch
  };
}; //end list fetcher