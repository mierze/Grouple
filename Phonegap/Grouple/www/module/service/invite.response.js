'use strict'
module.exports = function($http)
{ //invite responder
  var respond = function(post, type, callback)
  { //start send
    this.url = "http://mierze.gear.host/grouple/api/" + type + ".php";
    alert(JSON.stringify(post));
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : this.url,
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end send
  return {
    respond: respond
  };
}; //end invite responder
