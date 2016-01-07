'use strict'
function ImageGetter(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(email, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/user/profile/image/' + email, callback);
  }
}

module.exports = ImageGetter;