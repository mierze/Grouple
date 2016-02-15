'use strict'
function Groups(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(email, content, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/group/list/' + content + '/' + email, callback);
  }
}

module.exports = Groups;