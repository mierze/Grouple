'use strict'
function Register(Poster) {
  this.register = register;

  return {
    register: this.register
  };

  function register(data, callback) {
    Poster.post('http://groupleapp.herokuapp.com/api/session/register/', data, callback);
  }
}

module.exports = Register;