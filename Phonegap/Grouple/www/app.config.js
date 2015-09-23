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
            url:'/user-profile:id',
            templateUrl: 'profile/user-profile.html',
            controller: 'ProfileController'
        })
        .state('group-profile', {
            url:'/group-profile:id',
            templateUrl: 'profile/group-profile.html',
            controller: 'ProfileController'
        })
        .state('event-profile', {
            url:'/event-profile:id',
            templateUrl: 'profile/event-profile.html',
            controller: 'ProfileController'
        })
        .state('user-list', {
            url:'/user-list:content?id',
            templateUrl: 'list/user-list.html',
            controller: 'ListController'
        })
        .state('group-list', {
            url:'/group-list:content?id',
            templateUrl: 'list/group-list.html',
            controller: 'ListController'
        })
        .state('event-list', {
            url:'/event-list:content?id',
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
        .state('messages', {
            url:'/messages:id',
            templateUrl: 'message/messages.html',
            controller: 'MessageController'
        });
  });
})(); //end wrap
