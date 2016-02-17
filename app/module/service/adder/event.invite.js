'use strict'
function EventInviter(Poster) {
    var storage = window.localStorage;
    this.send = send;

    return {
        send: this.send
    };

    function send(data, callback) {
        data.from = storage.getItem('email');
        Poster.post('http://groupleapp.herokuapp.com/event/invite/', data, callback);
    }
} //end event inviter

module.exports = EventInviter;
