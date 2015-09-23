(function() //wrap
{
  //session storage
  var storage = window.localStorage;
  var Creater = function($http)
  { //Creater takes a group/event type, info and creates it in the db
    var create = function(post, type, callback)
    {
      this.url = 'http://mierze.gear.host/grouple/api/create_' + type + '.php';
      $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : this.url,
        data    : post
       }).then(
      function(result) {
        return callback(result.data);
      });
    };
    return {
      create: create
    };
  }; //end Creater
  angular.module('service').factory('Creater', Creater);
})(); //end wrap
