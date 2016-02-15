'use strict'
function FriendInviter(Poster) {
    //friend inviter takes in a to and from and sends the invite
    this.send = send;

    function send (data, callback) { //send function
        Poster.post('https://groupleapp.herokuapp.com/api/user/invite', data, callback);
    } //end send function
    return {
        send: this.send
    };
} //end friend inviter

module.exports = FriendInviter;
