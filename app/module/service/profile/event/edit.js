'use strict'
function EventEditer(Poster) {
  this.edit = edit;

  return {
    edit: this.edit
  };

  function edit(post, callback) {
    Poster.post(post, 'http://localhost:1337/api/event/profile/edit', callback);
  }
}

module.exports = EventEditer;
