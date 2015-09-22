(function() //wrap
{
  angular.module('grouple')
  .config(function($stateProvider, $urlRouterProvider)
  {
    $urlRouterProvider.otherwise('/');
    //all possible states
    $stateProvider
        .state('home', {
            url:'/',
            templateUrl: 'landing/home.html'
        })
        .state('login', {
            url:'/login',
            templateUrl: 'session/login.html',
            controller: 'LoginController'
        })
        .state('register', {
            url:'/register',
            templateUrl: 'session/register.html',
            controller: 'RegisterController'
        })
        .state('settings', {
            url:'/settings',
            templateUrl: 'session/settings.html'/*,
            controller: 'SettingsController'*/
        })
        .state('friends', {
            url:'/friends',
            templateUrl: 'landing/friends.html'
        })
        .state('groups', {
            url:'/groups',
            templateUrl: 'landing/groups.html'
        })
        .state('events', {
            url:'/events',
            templateUrl: 'landing/events.html'
        })
        .state('user-profile', {
            url:'/user-profile',
            templateUrl: 'profile/user-profile.html',
            controller: 'ProfileController'
        })
        .state('group-profile', {
            url:'/group-profile',
            templateUrl: 'profile/group-profile.html',
            controller: 'ProfileController'
        })
        .state('event-profile', {
            url:'/event-profile',
            templateUrl: 'profile/event-profile.html',
            controller: 'ProfileController'
        })
        .state('friend-list', {
            url:'/friend-list',
            templateUrl: 'list/user-list.html',
            controller: 'ListController',
            params: { content: 'friends'}
        })
        .state('group-list', {
            url:'/group-list',
            templateUrl: 'list/group-list.html',
            controller: 'ListController'
        })
        .state('event-list', {
            url:'/event-list',
            templateUrl: 'list/event-list.html',
            controller: 'ListController'
        })
        .state('group-create', {
            url:'/group-create',
            templateUrl: 'landing/group-create.html',
            controller: 'GroupCreateController'
        })
        .state('event-create', {
            url:'/event-create',
            templateUrl: 'landing/event-create.html',
            controller: 'EventCreateController'
        })
        .state('contacts', {
            url:'/contacts',
            templateUrl: 'message/contacts.html',
            controller: 'ContactController'
        })
        .state('user-messages', {
            url:'/user-messages',
            templateUrl: 'message/messages.html',
            controller: 'MessageController'
        });
  });
})(); //end wrap
