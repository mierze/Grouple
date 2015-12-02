'use strict'
module.exports = function($stateProvider, $urlRouterProvider)
{ //routes
  //all possible states
  $stateProvider
    .state('home', {
        url:'/',
        templateUrl: 'module/layout/home.html',
        controller: 'HomeController as vm'
    })
    .state('login', {
        url:'/login',
        templateUrl: 'module/session/login/layout.html',
        controller: 'LoginController as vm'
    })
    .state('register', {
        url:'/register',
        templateUrl: 'module/session/register/layout.html',
        controller: 'RegisterController as vm'
    })
    .state('settings', {
        url:'/settings',
        templateUrl: 'module/session/settings/layout.html'/*,
        controller: 'SettingsController'*/
    })
    .state('friends', {
        url:'/friends',
        templateUrl: 'module/layout/friends.html',
        controller: 'FriendsController as vm'
    })
    .state('groups', {
        url:'/groups',
        templateUrl: 'module/layout/groups.html',
        controller: 'GroupsController as vm'
    })
    .state('events', {
        url:'/events',
        templateUrl: 'module/layout/events.html',
        controller: 'EventsController as vm'
    })
    .state('user-profile', {
        url:'/user-profile/:id',
        templateUrl: 'module/profile/user/layout.html',
        controller: 'UserProfileController as vm'
    })
    .state('group-profile', {
        url:'/group-profile/:id',
        templateUrl: 'module/profile/group/layout.html',
        controller: 'GroupProfileController as vm'
    })
    .state('event-profile', {
        url:'/event-profile/:id',
        templateUrl: 'module/profile/event/layout.html',
        controller: 'EventProfileController as vm'
    })
    //TODO: different states for each content
    .state('user-list', {
        url:'/user-list/:content?id',
        templateUrl: 'module/list/user/layout.html',
        controller: 'UserListController as vm'
    })
    .state('group-list', {
        url:'/group-list/:content?id',
        templateUrl: 'module/list/group/layout.html',
        controller: 'GroupListController as vm'
    })
    .state('event-list', {
        url:'/event-list/:content?id',
        templateUrl: 'module/list/event/layout.html',
        controller: 'EventListController as vm'
    })
    .state('badge-list', {
        url:'/badge-list/:id',
        templateUrl: 'module/list/badge/layout.html',
        controller: 'BadgeListController as vm'
    })
    .state('group-create', {
        url:'/group-create',
        templateUrl: 'module/adder/group/create/layout.html',
        controller: 'GroupCreateController as vm'
    })
    .state('event-create', {
        url:'/event-create',
        templateUrl: 'module/adder/event/create/layout.html',
        controller: 'EventCreateController as vm'
    }) 
    .state('group-invite', {
        url:'/group-invite/:id',
        templateUrl: 'module/adder/group/invite/layout.html',
        controller: 'GroupInviteController as vm'
    })
    .state('event-invite', {
        url:'/event-invite:id',
        templateUrl: 'module/adder/event/invite/layout.html',
        controller: 'EventInviteController as vm'
    })
    .state('contacts', {
        url:'/contacts',
        templateUrl: 'module/message/contact/layout.html',
        controller: 'ContactController as vm'
    })
    .state('user-messages', {
        url:'/user-messages/:id',
        templateUrl: 'module/message/user/layout.html',
        controller: 'UserMessageController as vm'
    })
    .state('entity-messages', {
        url:'/entity-messages/:content?id',
        templateUrl: 'module/message/entity/layout.html',
        controller: 'EntityMessageController as vm'
    });
  $urlRouterProvider.otherwise('/login');
}; //end routes
