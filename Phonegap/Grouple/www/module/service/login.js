(function()
{ //wrap
  var Login = function($http)
  { //login is a service that queries the server and returns the response for the attempted login
    var login = function(post, callback)
    { //start edit
      $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : "http://mierze.gear.host/grouple/api/login.php",
        data    : post
       }).then(
      function(result) {
        return callback(result.data);
      });
    }; //end edit
    return {
      login: login
    };
  }; //end login
  angular.module('service').factory('Login', Login);
})(); //end wrap
