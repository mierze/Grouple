'use strict'
function Poster($http) {
  //poster
  var storage = window.localStorage;
  var post = function(post, callback) {
    post.from = storage.getItem('email');
    $http({
      method  : 'POST',
      url     : 'http://grouple.gear.host/api/invite_to_event.php',
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end post function
  return {
    post: post
  };
}; //end poster

module.exports = Poster;