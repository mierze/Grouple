'use strict'
function EventInviter(Poster) {
    var storage = window.localStorage;
    this.send = send;

    return {
        send: this.send
    };

    function send(data, callback) {
        data.from = storage.getItem('email');
        Poster.post('http://localhost:1337/event/invite/', data, callback);
    }
} //end event inviter

module.exports = EventInviter;
