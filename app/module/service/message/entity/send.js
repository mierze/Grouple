'use strict'
function EntityMessageSender(Poster) {
  this.send = send;

  return {
    send: this.send
  };

  var send = function(post, type, callback) {
    Poster.post('http://groupleapp.herokuapp.com/api/' + type + '/message/send', post, callback);
  }
}

module.exports = EntityMessageSender;
