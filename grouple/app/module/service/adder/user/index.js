//TODO
//[ ] refactor out all extra calls, strip down to just setting bare url and params
//for list fetcher, one file w/ switch, or seperate files with one liner?

'use strict';
module.exports = angular.module('service.adder', [])
  .factory('Getter', require('./get'))
  .factory('Creator', require('./create'))
  .factory('FriendInviter', require('./friend.invite'))
  .factory('GroupInviter', require('./group.invite'))
  .factory('EventInviter', require('./event.invite'))
  .factory('ImageFetcher', require('./image.fetch'))
  .factory('InviteResponder', require('./invite.respond'))
  .factory('ListFetcher', require('./list.fetch'))
  .factory('Login', require('./login'))
  .factory('MessageFetcher', require('./message.fetch'))
  .factory('MessageSender', require('./message.send'))
  .factory('ProfileEditer', require('./profile.edit'))
  .factory('UserProfileFetcher', require('./user-profile.fetch'))
  .factory('ProfileFetcher', require('./profile.fetch')) //group / event TODO
  //.factory('GroupProfileFetcher', require('./get.js'))
  //.factory('EventProfileFetcher', require('./get.js'))
  .factory('Register', require('./register'))
  .factory('SettingsFetcher', require('./settings.fetch'));
