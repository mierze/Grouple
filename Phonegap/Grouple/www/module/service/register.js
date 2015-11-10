'use strict'
module.exports = function($http)
{ //register is a service responsible for registering an account on the db
  var register = function(post, callback)
  { //register function
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : 'http://mierze.gear.host/grouple/api/register.php',
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end register function
  return {
    register: register
  };
}; //end register
