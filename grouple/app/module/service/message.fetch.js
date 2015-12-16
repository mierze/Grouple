'use strict'
module.exports = function($http) {
    //message fetcher takes in a type and returns the messages for that entity
    var fetch = function(params, type, callback) {
        var url = 'https://groupleapp.herokuapp.com/api/';
        if (type === 'contacts')
            url += ('user/messages/contacts/' + params.email);
        else if (type === 'user')
            url += (type + '/messages/' + params.email + '/' + params.contact);
        else //group, event
            url += (type + '/messages/' + params.id);
        $http({
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
