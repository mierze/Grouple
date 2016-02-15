'use strict';
module.exports = angular.module('service.adder', [])
  .factory('Creator', require('./create'))
  .factory('FriendInviter', require('./friend.invite'))
  .factory('GroupInviter', require('./group.invite'))
  .factory('EventInviter', require('./event.invite'))
  .factory('InviteResponder', require('./invite.respond'));
