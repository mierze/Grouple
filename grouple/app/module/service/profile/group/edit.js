'use strict'
function Editer(Poster) {
  this.edit = edit;
  
  return {
    edit: this.edit
  };
  
  function edit(post, callback) {  
    Poster.post(post, 'https://groupleapp.herokuapp.com/api/group/profile/edit', callback);
  }
}

module.exports = Editer;
