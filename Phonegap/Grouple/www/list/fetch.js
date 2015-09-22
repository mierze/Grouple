(function() //wrap
{
  var storage = window.localStorage;
  /*********************************************
  *************** SERVICES BELOW ***************
  *********************************************/
  //PANDA -> move these to seperate files userData, eventData, groupData
  //ListFetcher: takes in a content type for the list and fetches and returns the corresponding list
  angular.module('service')
  .factory('ListFetcher', function($http)
  { //ListFetcher is a service for fetching any type of list in grouple
    var fetch = function(type, callback)
    {
      alert("FETCHING NOW");
      this.url = "http://mierze.gear.host/grouple/api/get_" + type + ".php";
      this.post = {};
       // if (getVal("id") != null)
       //   this.post.id = getVal("id");
        //else if (getVal("email") != null)
       //   this.post.email = getVal("email");
       // else
       //PANDA: need to get ids and some kind of trigger to signal
          this.post.email = storage.getItem("email");
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
  });
    /*var fetch = function(type, callback)
    {
      this.url = "http://mierze.gear.host/grouple/api/get_" + type + ".php";
      this.post = {};
      if (getVal("id") != null)
        this.post.id = getVal("id");
      else if (getVal("email") != null)
        this.post.email = getVal("email");
      else
        this.post.email = storage.getItem("email");
        return "test";
      //return $http(*/
     // { //http request to fetch list from server PANDA refactor out this
     //   method  : 'POST',
     //   url     : this.url,
      //  data    : this.post
     // }).then(
      //  function(result) {
      //    callback(result.data);
     // });
  //  };
  /**    var object =
      {
        fetch: function (type, callback)
        {
          return fetch(type, callback);
        }
      };
    return object; 
  }*/
  /*
  angular.module('services', [])
    .factory('ListFetcher', services);
    */
})(); //end wrap
