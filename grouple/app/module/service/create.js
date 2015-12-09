'use strict'
module.exports = function($http)
{ //creater takes a group/event type, info and creates it in the db
  var create = function(post, type, callback)
  { //start create
    var url = 'https://groupleapp.herokuapp.com/api/' + type + '/create';
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
