'use strict'
module.exports = function($http)
{ //list fetcher is a service for fetching any type of list in grouple
  var fetch = function(params, type, callback)
  { //start fetch
    var url = 'https://groupleapp.herokuapp.com/api';
    switch (type)
    {
      case 'friends':
        url += '/user/friends';
        break;
      case 'friend_invites':
        url += '/user/friends/invites';
        break;
      case 'groups':
        url += '/user/groups';
        break;
      case 'group_invites':
        url += '/user/groups/invites';
        break;
      case 'group_members':
        url += '/group/members';
        break;
      case 'event_invites':
        url += '/user/events/invites';
        break;
      case 'events_upcoming':
        url += '/user/events/upcoming';
        break;
      case 'events_pending':
      url += '/user/events/pending';
        break;
      case 'events_past':
        url += '/user/events/past';
        break;
      case 'events_declined':
        url += '/user/events/declined';
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