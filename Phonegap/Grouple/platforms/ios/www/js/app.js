(function() //wrap
{
  //PANDA: look at liquids and think of a good way to store sessions
    //[ ] refactor into services and more directives and seperate files for directives and services
  var storage = window.localStorage;
  //declare main grouple module / storage
  angular.module('grouple', ['controllers', 'userControllers', 'directives', 'ui.router'])
  
  .config(function($stateProvider, $urlRouterProvider)
  {
    $urlRouterProvider.otherwise('/');
    //all possible states
    $stateProvider
        .state('home', {
            url:'/',
            templateUrl: '../home.html'
        })
        .state('login', {
            url:'/login',
            templateUrl: '../user/account/login.html',
            controller: 'LoginController'
        })
        .state('register', {
            url:'/register',
            templateUrl: '../user/account/register.html',
            controller: 'RegisterController'
        })
        .state('friends', {
            url:'/friends',
            templateUrl: '../user/friends.html'
        })
        .state('groups', {
            url:'/groups',
            templateUrl: '../group/groups.html'
        })
        .state('events', {
            url:'/events',
            templateUrl: '../event/events.html'
        })
        .state('user-profile', {
            url:'/user-profile',
            templateUrl: '../user/user-profile.html',
            controller: 'ProfileController'
        })
        .state('group-profile', {
            url:'/group-profile',
            templateUrl: '../group/group-profile.html',
            controller: 'ProfileController'
        })
        .state('event-profile', {
            url:'/event-profile',
            templateUrl: '../event/event-profile.html',
            controller: 'ProfileController'
        })
        .state('user-list', {
            url:'/user-list',
            templateUrl: '../user/user-list.html',
            controller: 'ListController'
        })
        .state('group-list', {
            url:'/group-list',
            templateUrl: '../group/group-list.html',
            controller: 'ListController'
        })
        .state('event-list', {
            url:'/event-list',
            templateUrl: '../event/event-list.html',
            controller: 'ListController'
        })
        .state('group-create', {
            url:'/group-create',
            templateUrl: '../group/group-create.html',
            controller: 'GroupCreateController'
        })
        .state('event-create', {
            url:'/event-create',
            templateUrl: '../event/event-create.html',
            controller: 'EventCreateController'
        });
  }])
  /*
   *PANDA: ui-sref="home" or in controller $state.go('about')*/
  .factory('ListFetcher', function($http)
  {
    alert("IN HERE");
   var fetch = function(type, callback)
    {
      this.url = "http://mierze.gear.host/grouple/api/get_" + type + ".php";
      alert(this.url);
      this.post = {};
      if (getVal("id") != null)
        this.post.id = getVal("id");
      else if (getVal("email") != null)
        this.post.email = getVal("email");
      else
        this.post.email = storage.getItem("email");
      return $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : this.url,
        data    : this.post
       }).then(
        function(result) {
          callback(result.data);
      });
    };
  });
  

  /*********************************************
  *************** FUNCTIONS BELOW ***************
  *********************************************/
  //function to get parameters from url, takes in key and returns value if exists, or null
  this.getVal = function(key)
  {
    var result = null;
    window.location.search.substr(1).split("&").forEach(function (item)
    {
      var keySet = item.split("=");
      if (keySet[0] === key)
      {
        result = keySet[1];
      }
    });
    return result;
  }
  
  //modal visibility toggle functions below
  this.showAddFriend = function()
  {
    document.getElementById('addfriend-modal').style.display = 'block';
  };
  this.closeAddFriend = function()
  {
    document.getElementById('addfriend-modal').style.display = 'none';
  };
  this.showEditProfile = function()
  {
    document.getElementById('editprofile-modal').style.display = 'block';
  };
 this.closeEditProfile = function()
  {
    document.getElementById('editprofile-modal').style.display = 'none';
  };
  this.showEventCreate = function()
  {
    document.getElementById('event-create').style.display = 'block';
  };
  this.closeEventCreate = function()
  {
    document.getElementById('event-create').style.display = 'none';
  };
  
  //function to handling clearing memory and logging out user
  this.logout = function()
  {
    storage.clear(); //clear storage
    document.location.href="login.html";
    alert("Later playa!");
  };
})(); //end wrap
