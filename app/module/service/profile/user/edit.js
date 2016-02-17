'use strict'
function UserEditer(Poster) {
  this.edit = edit;

  return {
    edit: this.edit
  };

  function edit(post, callback) {
    Poster.post('http://localhost:1337/api/user/profile/edit/', post, callback);
  }
}

module.exports = UserEditer;
