(function() //wrap
{
    var storage = window.localStorage;
    /*********************************************
    *************** SERVICES BELOW ***************
    *********************************************/
    angular.module('service')
    .factory('ProfileFetcher', function($http)
    { //ListFetcher is a service for fetching any type of list in grouple
        var fetch = function(type, callback)
        {
            this.post = {};
            this.url = "http://mierze.gear.host/grouple/api/get_" + type + "_info.php";
            //PANDA setup post and url
            if (type === 'user')
            {
                this.post.email = storage.getItem("email");
            }
            $http(
            { //http request to fetch list from server PANDA refactor out this
              method  : 'POST',
              url     : this.url,
              data    : this.post
             }).then(
            function(result) {
              return callback(result.data);
            });
        };
        return {
            fetch: fetch
        };
    });
})(); //end wrap
