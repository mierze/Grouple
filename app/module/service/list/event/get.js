'use strict'
function Events(Getter) {
  this.get = get;

  return {
    get: this.get
  };

  function get(id, content, callback) {
    Getter.get('http://localhost:1337/api/event/list/' + content + '/' + id, callback);
  }
}

module.exports = Events;
