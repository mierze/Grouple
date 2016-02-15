'use strict'
function GroupInviter($http) {
  var storage = window.localStorage;
  var send = function(post, callback) {
    //TODO: need to take in key-vals email->role
    post.from = storage.getItem('email');
    alert("Post in group inviter" + JSON.stringify(post));
    $http({ //http request to fetch list from server PANDA refactor out this
      method  : 'POST',
      url     : 'http://grouple.gear.host/api/invite_to_group.php',
      data    : post
     }).then(
    function(result) {
      return callback(result.data);
    });
    }; //end send function
  return {
    send: send
  };
} //end group inviter

module.exports = GroupInviter;
