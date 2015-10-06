'use strict'
module.exports = function($http)
{ //image fetcher
  var fetch = function(post, type, callback)
  { //start fetch
    this.url = "http://mierze.gear.host/grouple/api/get_profile_image_hdpi.php";
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : this.url,
      data    : post
    }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end fetch
  return {
    fetch: fetch
  };
}; //end image fetcher
