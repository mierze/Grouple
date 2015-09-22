(function() //wrap
{
  //PANDA: look at liquids and think of a good way to store sessions
  var storage = window.localStorage;
  //declare main grouple module / storage
  angular.module('session', []);
  angular.module('list', []);
  angular.module('profile', []);
  angular.module('message', []);
  angular.module('service', []);
  angular.module('directive', []);
  angular.module('grouple', ['session', 'list', 'profile', 'message', 'service', 'directive', 'ui.router']); 
  //CHANGING SCREENS -> ui-sref="home" or in controller $state.go('home')
})(); //end wrap
