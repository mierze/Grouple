'use strict'
function Events(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(id, content, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/event/list/' + content + '/' + id, callback);
  }
}

module.exports = Events;