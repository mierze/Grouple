'use strict'
function Users(Getter) {
  this.get = get;

  return {
    get: this.get
  };
  
  function get(id, content, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/user/list/' + content + '/' + id, callback);
  }
}

module.exports = Users;