'use strict'
module.exports = function($http)
{ //profile editer takes in updated info and a type and makes the proper updates
  var edit = function(post, type, callback)
  { //edit function
    this.url = "http://mierze.gear.host/grouple/api/edit_" + type + ".php";
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : this.url,
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end edit function
  return {
    edit: edit
  };
}; //end profile editer
