//TODO
//[ ] refactor out all extra calls, strip down to just setting bare url and params
//for list fetcher, one file w/ switch, or seperate files with one liner?

'use strict';
module.exports = angular.module('service.adder', [])
  .factory('Creator', require('./create'))
  .factory('FriendInviter', require('./friend.invite'))
  .factory('GroupInviter', require('./group.invite'))
  .factory('EventInviter', require('./event.invite'))
  .factory('InviteResponder', require('./invite.respond'));
