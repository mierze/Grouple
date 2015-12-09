'use strict'
module.exports = function($http)
{ //message fetcher takes in a type and returns the messages for that entity
    var fetch = function(params, type, callback)
    { //fetch function
        var url = 'https://groupleapp.herokuapp.com/api/';
        if (type === 'contacts')
            var url = 'user/messages/contacts/';
        else
            url += type + '/messages/';
        url += params.id;
        $http(
        { //http request to fetch list from server PANDA refactor out this
            method  : 'GET',
            url     : url
         }).then(
        function(result) {
            return callback(result.data);
        });
    }; //end fetch function
    return {
        fetch: fetch
    };
}; //end message fetcher
