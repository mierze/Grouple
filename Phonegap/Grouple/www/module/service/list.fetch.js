(function() 
{ //wrap
  var ListFetcher = function($http)
  { //list fetcher is a service for fetching any type of list in grouple
    var fetch = function(post, type, callback)
    { //start fetch
      this.url = "http://mierze.gear.host/grouple/api/get_" + type + ".php";
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
  angular.module('service').factory('ListFetcher', ListFetcher);
})(); //end wrap
