'use strict'
module.exports = function($http)
{ //register is a service responsible for registering an account on the db
  var register = function(post, callback)
  { //register function
    $http(
    { //http post for registering account
      method  : 'POST',
      url     : 'http://groupleapp.herokuapp.com/api/session/register',
      data    : post
     }).then(
    function(result) {
      alert(JSON.stringify(result.data));
      return callback(result.data);
    });
  }; //end register function
  return {
    register: register
  };
}; //end register
