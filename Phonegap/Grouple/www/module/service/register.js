(function()
{ //wrap
  var Register = function($http)
  { //register is a service responsible for registering an account on the db
    var register = function(post, callback)
    { //start edit
      $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : "http://mierze.gear.host/grouple/api/register.php",
        data    : post
       }).then(
      function(result) {
        return callback(result.data);
      });
    }; //end edit
    return {
      register: register
    };
  }; //end register
  angular.module('service').factory('Register', Register);
})(); //end wrap
