'use strict'
function Profile(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(email, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/user/profile/' + email, callback);
  }
}

module.exports = Profile;