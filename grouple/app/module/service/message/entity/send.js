'use strict'
function MessageSender(Poster) {
  this.send = send;
  
  return {
    send: this.send
  };
  
  var send = function(post, type, callback) {
    Poster.post('https://groupleapp.herokuapp.com/' + type + '/messages/send', post, callback);
  }
}

module.exports = MessageSender;

