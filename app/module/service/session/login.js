'use strict'
function Login(Poster) {
  this.login = login;

  return {
    login: login
  };

  function login(data, callback) {
    Poster.post('http://localhost:1337/api/session/login/', data, callback);
  }
}

module.exports = Login;
