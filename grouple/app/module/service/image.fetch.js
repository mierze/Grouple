'use strict'
module.exports = function($http)
{ //image fetcher
  var fetch = function(params, type, callback)
  { //start fetch
    params.content = type; //set type in post
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'GET',
      url     : 'https://groupleapp.herokuapp.com/api/user/profile/image/' + params.id
    }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end fetch
  return {
    fetch: fetch
  };
}; //end image fetcher
