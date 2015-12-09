'use strict'
module.exports = function($http)
{ //profile editer takes in updated user / group / event profile info and updates them
  var edit = function(post, type, callback)
  { //edit function
    var url = 'https://groupleapp.herokuapp.com/api/' + type + '/profile/edit';
    //http request to fetch list from server PANDA refactor out this
    $http(
    {
      method : 'POST',
      url : url,
      data : post
    }).then(
    function(result) {
      return callback(result.data);
    });      
  }; //end edit
  return {
      edit: edit
  };
}; //end profile editer