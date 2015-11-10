'use strict'
module.exports = function($http)
{ //message fetcher takes in a type and returns the messages for that entity
    var fetch = function(post, type, callback)
    { //fetch function
        if (type === 'user') 
            var url = 'http://mierze.gear.host/grouple/api/get_messages.php';
        else if (type === 'contacts')
            var url = 'http://mierze.gear.host/grouple/api/get_contacts.php';
        else
            var url = 'http://mierze.gear.host/grouple/api/get_' + type + '_messages.php';
        $http(
        { //http request to fetch list from server PANDA refactor out this
            method  : 'POST',
            url     : url,
            data    : post
         }).then(
        function(result) {
            return callback(result.data);
        });
    }; //end fetch function
    return {
        fetch: fetch
    };
}; //end message fetcher
