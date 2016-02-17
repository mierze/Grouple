'use strict'
function GroupInviter(Poster) {
    var storage = window.localStorage;
    this.send = send;

    return {
      send: this.send
    };

    function send(data, callback) {
        data.from = storage.getItem('email');
        Poster.post('http://localhost:1337/group/invite/', data, callback);
    }
} //end group inviter

module.exports = GroupInviter;
