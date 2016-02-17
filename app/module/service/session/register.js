'use strict'
function Register(Poster) {
  this.register = register;

  return {
    register: this.register
  };

  function register(data, callback) {
    Poster.post('http://localhost:1337/api/session/register/', data, callback);
  }
}

module.exports = Register;
