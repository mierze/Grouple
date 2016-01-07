'use strict'
function ImageGetter(Getter) {
  this.get = get;
  
  return {
    get: this.get
  };
  
  function get(id, callback) {
    Getter.get('https://groupleapp.herokuapp.com/api/event/profile/image/' + id, callback);
  }
}

module.exports = ImageGetter;