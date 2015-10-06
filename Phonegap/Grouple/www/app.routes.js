'use strict'
module.exports = function($stateProvider, $urlRouterProvider)
{ //routes
  $urlRouterProvider.otherwise('/login');
  //all possible states
  $stateProvider
      .state('home', {
          url:'/',
          templateUrl: 'module/layout/home.html'
      })
      .state('login', {
          url:'/login',
          templateUrl: 'module/session/login/layout.html',
          controller: 'LoginController'
      })
      .state('register', {
          url:'/register',
          templateUrl: 'module/session/register/layout.html',
          controller: 'RegisterController'
      })
      .state('settings', {
          url:'/settings',
          templateUrl: 'module/session/settings/layout.html'/*,
          controller: 'SettingsController'*/
      })
      .state('friends', {
          url:'/friends',
          templateUrl: 'module/layout/friends.html',
          controller: 'FriendInviteController'
      })
      .state('groups', {
          url:'/groups',
          templateUrl: 'module/layout/groups.html'
      })
      .state('events', {
          url:'/events',
          templateUrl: 'module/layout/events.html'
      })
      .state('user-profile', {
          url:'/user-profile:id',
          templateUrl: 'module/profile/user/layout.html',
          controller: 'ProfileController'
      })
      .state('group-profile', {
          url:'/group-profile:id',
          templateUrl: 'module/profile/group/layout.html',
          controller: 'ProfileController'
      })
      .state('event-profile', {
          url:'/event-profile:id',
          templateUrl: 'module/profile/event/layout.html',
          controller: 'ProfileController'
      })
      .state('user-list', {
          url:'/user-list:content?id',
          templateUrl: 'module/list/user/layout.html',
          controller: 'ListController'
      })
      .state('group-list', {
          url:'/group-list:content?id',
          templateUrl: 'module/list/group/layout.html',
          controller: 'ListController'
      })
      .state('event-list', {
          url:'/event-list:content?id',
          templateUrl: 'module/list/event/layout.html',
          controller: 'ListController'
      })
      .state('group-create', {
          url:'/group-create',
          templateUrl: 'module/adder/group-create/layout.html',
          controller: 'GroupCreateController'
      })
      .state('event-create', {
          url:'/event-create',
          templateUrl: 'module/adder/event-create/layout.html',
          controller: 'EventCreateController'
      })
      .state('contacts', {
          url:'/contacts',
          templateUrl: 'module/message/contact/layout.html',
          controller: 'ContactController'
      })
      .state('messages', {
          url:'/messages:id',
          templateUrl: 'module/message/message/layout.html',
          controller: 'MessageController'
      });
}; //end routes
