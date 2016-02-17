'use strict'
function Creator(Poster) {
    this.create = create;

  return {
    create: this.create
  };

  function create(post, type, callback) {
    //start create
    var url = 'http://groupleapp.herokuapp.com/api/' + type + '/create';
    Poster.post(url, post, callback);
  } //end create
} //end creator

module.exports = Creator;
