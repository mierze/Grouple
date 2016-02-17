'use strict'
function GroupEditer(Poster) {
  this.edit = edit;

  return {
    edit: this.edit
  };

  function edit(post, callback) {
    Poster.post(post, 'http://localhost:1337/api/group/profile/edit', callback);
  }
}

module.exports = GroupEditer;
