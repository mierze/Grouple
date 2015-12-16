'use strict'

module.exports = function($http) {
    //getter
    var get = function(url, callback) {
      $http({
          method  : 'GET',
          url     : url
       }).then(
      function(result) {
          return callback(result.data);
      });
    }; //end fetch function
    return {
      get: get
    };
  }; //end getter



//module.exports = Getter;