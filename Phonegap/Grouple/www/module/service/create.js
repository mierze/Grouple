'use strict'
module.exports = function($http)
{ //creater takes a group/event type, info and creates it in the db
  var create = function(post, type, callback)
  { //start create
    var url = 'http://mierze.gear.host/grouple/api/create_' + type + '.php';
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : url,
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end create
  return {
    create: create
  };
}; //end creater
