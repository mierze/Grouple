'use strict'
function Users(Getter) {
  this.get = get;

  return {
    get: this.get
  };
  
  function get(id, content, callback) {
    Getter.get('http://localhost:1337/api/user/list/' + content + '/' + id, callback);
  }
}

module.exports = Users;