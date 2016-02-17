'use strict'
function GroupInviter(Poster) {
    var storage = window.localStorage;
    this.send = send;

    return {
      send: this.send
    };

    function send(data, callback) {
        data.from = storage.getItem('email');
        Poster.post('http://groupleapp.herokuapp.com/group/invite/', data, callback);
    }
} //end group inviter

module.exports = GroupInviter;
