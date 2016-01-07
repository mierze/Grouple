'use strict'
function Register(Poster) {
  this.register = register;
  
  return {
    register: register
  };
  
  function register(data, callback) {
    Poster.post('https://groupleapp.herokuapp.com/api/session/register', data, callback);
  }
}

module.exports = Register;
