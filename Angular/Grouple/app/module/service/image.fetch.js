'use strict'
module.exports = function($http)
{ //image fetcher
  var fetch = function(params, type, callback)
  { //start fetch
    params.content = type; //set type in post
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'GET',
      url     : 'http://grouple.gear.host/api/get_profile_image_hdpi.php',
      params    : params
    }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end fetch
  return {
    fetch: fetch
  };
}; //end image fetcher
