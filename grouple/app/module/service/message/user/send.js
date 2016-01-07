'use strict'
function MessageSender(Poster) {
  this.send = send;
  
  return {
    send: this.send
  };
  
  var send = function(post, callback) {
    Poster.post('https://groupleapp.herokuapp.com/user/messages/send', post, callback);
  }
}

module.exports = MessageSender;

