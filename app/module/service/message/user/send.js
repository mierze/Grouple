'use strict'
function MessageSender(Poster) {
  this.send = send;

  return {
    send: this.send
  };

  var send = function(post, callback) {
    Poster.post('http://localhost:1337/api/user/message/send', post, callback);
  }
}

module.exports = MessageSender;
