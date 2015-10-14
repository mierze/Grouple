'use strict'
module.exports = function($http)
{ //list fetcher is a service for fetching any type of list in grouple
  var fetch = function(post, type, callback)
  { //start fetch
    this.url = "http://mierze.gear.host/grouple/api/get_" + type + ".php";
    
    alert(this.url + "\n" + JSON.stringify(post));
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
}; //end list fetcher
