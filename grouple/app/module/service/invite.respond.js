'use strict'
module.exports = function($http)
{ //invite responder
  var respond = function(post, decision, content, callback)
  { //start send
    if (decision === 'accept')
    {
       var url = 'http://grouple.gear.host/api/' + decision + '_' + content + '_invite.php';
    }
    else
    {
      post.type = 'decline'; //for api to return proper message
      var url = 'http://grouple.gear.host/api/leave_' + content + '.php';
    }
    $http(
    { //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : url,
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
  }; //end send
  return {
    respond: respond
  };
}; //end invite responder
