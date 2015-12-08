'use strict'
module.exports = function($http)
{ //profile fetcher takes in an id and type and returns the corresponding user/group/event profile
    var fetch = function(params, type, callback)
    { //start fetch function
        var url = 'https://groupleapp.herokuapp.com/api/user/profile/';
        $http(
        { //http request to fetch list from server PANDA refactor out this
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
}; //end profile fetcher