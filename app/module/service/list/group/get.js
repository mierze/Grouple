'use strict'
function Groups(Getter) {
  this.get = get;

  return {
    get: this.get
  };

  function get(id, content, callback) {
    Getter.get('http://localhost:1337/api/group/list/' + content + '/' + id, callback);
  }
}

module.exports = Groups;
