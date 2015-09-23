(function() 
{ //wrap
    //session storage
    var storage = window.localStorage;
    var MessageFetcher = function($http)
    { //MessageFetcher takes in a type and returns the messages for that entity
        var fetch = function(post, type, callback)
        { //start fetch
            if (type === 'user') 
                this.url = "http://mierze.gear.host/grouple/api/get_messages.php";
            else if (type === 'contacts')
                this.url = "http://mierze.gear.host/grouple/api/get_contacts.php";
            else
                this.url = "http://mierze.gear.host/grouple/api/get_" + type + "_messages.php";
            post.user = storage.getItem("email");
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
    }; //end MessageFetcher
    angular.module('service', [])
        .factory('MessageFetcher', MessageFetcher);  
})(); //end wrap
