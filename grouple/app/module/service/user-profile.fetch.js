'use strict'
module.exports = function(Getter)
{ //profile fetcher takes in an id and type and returns the corresponding user/group/event profile
    var fetch = function(params, callback)
    { //start fetch function
        var url = 'https://groupleapp.herokuapp.com/api/user/profile/' + params.id;
        Getter.get(url, function(results)
        {
          return results;
        });
    }; //end profile fetcher
    return {
      fetch: fetch
    };
};