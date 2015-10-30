'use strict'
module.exports = function($http)
{ //profile fetcher takes in an id and type and returns the corresponding user/group/event profile
    var fetch = function(post, type, callback)
    { //start fetch function
        alert("here " + JSON.stringify(post));
        this.url = "http://mierze.gear.host/grouple/api/get_" + type + "_info.php";
        $http(
        { //http request to fetch list from server PANDA refactor out this
            method  : 'POST',
            url     : this.url,
            data    : post
         }).then(
        function(result) {
            return callback(result.data);
        });
    }; //end fetch
    return {
        fetch: fetch
    };
}; //end profile fetcher