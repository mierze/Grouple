'use strict'
function Getter($http) {
    this.get = get;
    
    return {
      get: this.get
    };
    
    function get(url, callback) {
        alert('in getter now ' + url);
        $http({
          method  : 'GET',
          url     : url
         }).then(
        function(result) {
          return callback(result.data);
        });
    }
}

module.exports = Getter;