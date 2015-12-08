'use strict'
module.exports = function($http)
{ //login is a service that queries the server and returns the response for the attempted login
  var login = function(post, callback)
  { //login function
    $http(
    { 
      method  : 'POST',
      url     : 'https://groupleapp.herokuapp.com/api/session/login/',
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
