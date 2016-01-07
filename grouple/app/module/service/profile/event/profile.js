'use strict'
function Profile(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(id, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/event/profile/' + id, callback);
  }
}

module.exports = Profile;