'use strict'
module.exports = function($http)
{ //register is a service responsible for registering an account on the db
  var register = function(post, callback)
  { //register function
    alert('post in service is ' + JSON.stringify(post));
    $http(
    { //http post for registering account
      method  : 'POST',
      url     : 'http://grouple.gear.host/api/register.php',
      data    : post
     }).then(
    function(result) {
      alert('got a result');
      alert(JSON.stringify(result.data));
      return callback(result.data);
    });
  }; //end register function
  return {
    register: register
  };
}; //end register
