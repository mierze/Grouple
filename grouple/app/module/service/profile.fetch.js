'use strict'
module.exports = function($http)
{ //profile fetcher takes in an id and type and returns the corresponding user/group/event profile
    var fetch = function(params, type, callback)
    { //start fetch function
        var url = 'https://groupleapp.herokuapp.com/api/' + type + '/profile/' + params.id;
        $http({
            method  : 'GET',
            url     : url
         }).then(
        function(result) {
            return callback(result.data);
        });
    }; //end fetch
    return {
        fetch: fetch
    };
}; //end profile fetcher