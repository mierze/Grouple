'use strict'
function Badges(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(email, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/user/profile/badges/' + email, callback);
  }
}

module.exports = Badges;