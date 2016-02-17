'use strict'
function FriendInviter(Poster) {
    this.send = send;

    return {
        send: this.send
    };

    function send (data, callback) { //send function
        Poster.post('http://localhost:1337/api/user/invite/', data, callback);
    } //end send function
} //end friend inviter

module.exports = FriendInviter;
