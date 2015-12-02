'use strict'
module.exports = function($http)
{ //list fetcher is a service for fetching any type of list in grouple
  var fetch = function(params, type, callback)
  { //start fetch
    var url = 'http://grouple.gear.host/api/get_' + type + '.php';
    $http(
    {
      method  : 'GET',
      url     : url,
      params  : params
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end fetch
  return {
    fetch: fetch
  };
}; //end list fetcher
