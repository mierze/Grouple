'use strict'
function GroupEditer(Poster) {
  this.edit = edit;

  return {
    edit: this.edit
  };

  function edit(post, callback) {
    Poster.post(post, 'http://groupleapp.herokuapp.com/api/group/profile/edit', callback);
  }
}

module.exports = GroupEditer;
