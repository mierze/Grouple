'use strict'
module.exports = function($http)
{ //list fetcher is a service for fetching any type of list in grouple
  var fetch = function(params, type, callback)
  { //start fetch
    var url = 'https://groupleapp.herokuapp.com/api';
    switch (type)
    {
      case 'friends':
        url += '/user/list/friends';
        break;
      case 'friend_invites':
        url += '/user/list/invites';
        break;
      case 'groups':
        url += '/group/list';
        break;
      case 'group_invites':
        url += '/group/list/invites';
        break;
      case 'group_members':
        url += '/user/list/members';
        break;
      case 'event_attending':
        url += '/user/list/attending';
        break;
      case 'event_invites':
        url += '/event/list/invites';
        break;
      case 'events_upcoming':
        url += '/event/list/upcoming';
        break;
      case 'events_pending':
      url += '/event/list/pending';
        break;
      case 'events_past':
        url += '/event/list/past';
        break;
      case 'events_declined':
        url += '/event/list/declined';
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