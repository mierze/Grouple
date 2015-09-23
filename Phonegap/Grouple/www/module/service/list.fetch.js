(function() //wrap
{
  //session storage
  var storage = window.localStorage;
  var ListFetcher = function($http)
  { //ListFetcher is a service for fetching any type of list in grouple
    var fetch = function(type, id, callback)
    {
      this.url = "http://mierze.gear.host/grouple/api/get_" + type + ".php";
      this.post = {};
      if (id == null || (id.length < 2 && type === 'user'))
        this.post.id = storage.getItem("email");
      else
        this.post.id = id;
      this.post.user = storage.getItem("email");
      $http(
      { //http request to fetch list from server PANDA refactor out this
        method  : 'POST',
        url     : this.url,
        data    : this.post
       }).then(
      function(result) {
        return callback(result.data);
      });
    };
    return {
      fetch: fetch
    };
  };
  angular.module('service').factory('ListFetcher', ListFetcher);
})(); //end wrap
