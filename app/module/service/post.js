'use strict'
function Poster($http) {
  this.post = post;
  
  return {
    post: this.post
  };
  
  function post(url, data, callback) {
    $http({
      method  : 'POST',
      url     : url,
      data    : data
     }).then(
    function(result) {
      return callback(result.data);
    });
  }
}

module.exports = Poster;