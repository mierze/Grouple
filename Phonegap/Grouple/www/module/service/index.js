'use strict';
module.exports = angular.module('service', [])
.factory('Creater', require('./create.js'))
.factory('FriendInviter', require('./friend.invite.js'))
.factory('ImageFetcher', require('./image.fetch.js'))
.factory('InviteResponder', require('./invite.response.js'))
.factory('ListFetcher', require('./list.fetch.js'))
.factory('Login', require('./login.js'))
.factory('MessageFetcher', require('./message.fetch.js'))
.factory('MessageSender', require('./message.send.js'))
.factory('ProfileEditer', require('./profile.edit.js'))
.factory('ProfileFetcher', require('./profile.fetch.js'))
.factory('Register', require('./register.js'))
.factory('SettingsFetcher', require('./settings.fetch.js'));
