'use strict'
function InviteResponder(Poster) {
    this.respond = respond;

    return {
      respond: this.respond
    };

    function respond(data, decision, content, callback) {
        Poster.post('http://localhost:1337/api/' + content + '/list/action/' + decision + '-invite', data, callback);
    }
} //end invite responder

module.exports = InviteResponder;
