'use strict'
function Groups(Getter) {
  this.get = get;

  return {
    get: this.get
  };

  function get(id, content, callback) {
    Getter.get('http://groupleapp.herokuapp.com/api/group/list/' + content + '/' + id, callback);
  }
}

module.exports = Groups;
