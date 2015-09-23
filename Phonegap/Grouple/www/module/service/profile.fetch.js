(function() //wrap
{
    //session storage
    var storage = window.localStorage;
    var ProfileFetcher = function($http)
    { //ProfileFetcher takes in an id and type and returns the corresponding user/group/event profile
        var fetch = function(type, id, callback)
        { //PANDA decide whether or not to force id plus email take in, could return editable!****
            this.post = {};
            this.url = "http://mierze.gear.host/grouple/api/get_" + type + "_info.php";
            if (id.length < 2 && type === 'user')
                //case that id is for logged user's email
                this.post.id = storage.getItem("email");
            else
                this.post.id = id; 
            this.post.user = storage.getItem("email");
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
    };
    var ImageFetcher = function($http)
    { //PANDA switch this to grab the images
        var fetch = function(type, id, callback)
        { //PANDA decide whether or not to force id plus email take in, could return editable!****
                   //http request to get user image
                   //PANDA: need to tweak php for content types, make this accept group/event/user and also sizing, seperate into a service
                   /*$http(
                   {
                     method : 'POST',
                     url : 'http://mierze.gear.host/grouple/api/get_profile_image_hdpi.php',
                     data : $scope.post
                   }).success(function(data)
                   {
          
                   })
                   .error(function(data)
                   {
                     alert("Error contacting server.");
                   });*/
                   
            this.post = {};
            this.post.content = type;
            this.url = "http://mierze.gear.host/grouple/api/get_profile_image_hdpi.php"; 
            //this.post.content = content;
            //.editable = false; //dissalow editable priv
            //PANDA need the id
            if (id.length < 2 && type === 'user')
                //case that id is for logged user's email
                this.post.id = storage.getItem("email");
            else
                this.post.id = id; 
            this.post.user = storage.getItem("email");
            alert("URL:" + this.url + "\npost:" + JSON.stringify(this.post));
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
    };
    angular.module('service')
    .factory('ProfileFetcher', ProfileFetcher)
    .factory('ImageFetcher', ImageFetcher);
})(); //end wrap
