(function()
{ //wrap
  var ProfileEditer = function($http)
  { //profile editer takes in updated info and a type and makes the proper updates
    var edit = function(post, type, callback)
    { //start edit
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
    }; //end edit
    return {
      edit: edit
    };
  }; //end profile editer
  angular.module('service').factory('ProfileEditer', ProfileEditer);
})(); //end wrap
