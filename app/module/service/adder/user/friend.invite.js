'use strict'
function FriendInviter(Poster) {
    this.send = send;

    return {
      send: this.send
    };

    function send(data, callback) { //send function
        Poster.post('http://groupleapp.herokuapp.com/api/user/invite', data, callback);
    }
} //end friend inviter

module.exports = FriendInviter;
