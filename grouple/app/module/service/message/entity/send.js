'use strict'
function EntityMessageSender(Poster) {
  this.send = send;
  
  return {
    send: this.send
  };
  
  var send = function(post, type, callback) {
    Poster.post('https://groupleapp.herokuapp.com/' + type + '/message/send', post, callback);
  }
}

module.exports = EntityMessageSender;

