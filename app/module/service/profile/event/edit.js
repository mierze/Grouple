'use strict'
function EventEditer(Poster) {
  this.edit = edit;

  return {
    edit: this.edit
  };

  function edit(post, callback) {
    Poster.post(post, 'http://groupleapp.herokuapp.com/api/event/profile/edit', callback);
  }
}

module.exports = EventEditer;
