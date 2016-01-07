'use strict'
function Login(Poster) {
  this.login = login;
  
  return {
    login: login
  };
  
  function login(data, callback) {
    Poster.post('https://groupleapp.herokuapp.com/api/session/login/', data, callback);
  }
}

module.exports = Login;
