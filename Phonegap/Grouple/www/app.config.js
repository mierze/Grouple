(function() //wrap
{
  angular.module('grouple')
  .config(function($stateProvider, $urlRouterProvider)
  {
    $urlRouterProvider.otherwise('/login');
    //all possible states
    $stateProvider
        .state('home', {
            url:'/',
            templateUrl: 'module/layout/home.html'
        })
        .state('login', {
            url:'/login',
            templateUrl: 'module/session/layout/login.html',
            controller: 'LoginController'
        })
        .state('register', {
            url:'/register',
            templateUrl: 'module/session/layout/register.html',
            controller: 'RegisterController'
        })
        .state('settings', {
            url:'/settings',
            templateUrl: 'module/session/layout/settings.html'/*,
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
            templateUrl: 'module/profile/layout/user-profile.html',
            controller: 'ProfileController'
        })
        .state('group-profile', {
            url:'/group-profile:id',
            templateUrl: 'module/profile/layout/group-profile.html',
            controller: 'ProfileController'
        })
        .state('event-profile', {
            url:'/event-profile:id',
            templateUrl: 'module/profile/layout/event-profile.html',
            controller: 'ProfileController'
        })
        .state('user-list', {
            url:'/user-list:content?id',
            templateUrl: 'module/list/layout/user-list.html',
            controller: 'ListController'
        })
        .state('group-list', {
            url:'/group-list:content?id',
            templateUrl: 'module/list/layout/group-list.html',
            controller: 'ListController'
        })
        .state('event-list', {
            url:'/event-list:content?id',
            templateUrl: 'module/list/layout/event-list.html',
            controller: 'ListController'
        })
        .state('group-create', {
            url:'/group-create',
            templateUrl: 'module/adder/layout/group-create.html',
            controller: 'GroupCreateController'
        })
        .state('event-create', {
            url:'/event-create',
            templateUrl: 'module/adder/layout/event-create.html',
            controller: 'EventCreateController'
        })
        .state('contacts', {
            url:'/contacts',
            templateUrl: 'module/message/layout/contacts.html',
            controller: 'ContactController'
        })
        .state('messages', {
            url:'/messages:id',
            templateUrl: 'module/message/layout/messages.html',
            controller: 'MessageController'
        });
  });
})(); //end wrap
