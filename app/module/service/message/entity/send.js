'use strict'
function EntityMessageSender(Poster) {
  this.send = send;

  return {
    send: this.send
  };

  var send = function(post, type, callback) {
    Poster.post('http://localhost:1337/api/' + type + '/message/send', post, callback);
  }
}

module.exports = EntityMessageSender;
