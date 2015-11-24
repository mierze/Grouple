'use strict'
module.exports = function($http)
{ //login is a service that queries the server and returns the response for the attempted login
  var login = function(post, callback)
  { //login function
    $http(
    { 
      method  : 'POST',
      url     : 'http://mierze.gear.host/grouple/api/login.php',
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end login function
  return {
    login: login
  };
}; //end login
