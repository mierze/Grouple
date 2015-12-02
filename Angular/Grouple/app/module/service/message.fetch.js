'use strict'
module.exports = function($http)
{ //message fetcher takes in a type and returns the messages for that entity
    var fetch = function(params, type, callback)
    { //fetch function
        if (type === 'user') 
            var url = 'http://grouple.gear.host/api/get_messages.php';
        else if (type === 'contacts')
            var url = 'http://grouple.gear.host/api/get_contacts.php';
        else
            var url = 'http://grouple.gear.host/api/get_' + type + '_messages.php';
        $http(
        { //http request to fetch list from server PANDA refactor out this
            method  : 'GET',
            url     : url,
            params  : params
         }).then(
        function(result) {
            return callback(result.data);
        });
    }; //end fetch function
    return {
        fetch: fetch
    };
}; //end message fetcher
