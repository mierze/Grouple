'use strict'
function EventProfileGetter(Getter) {
  var vm = this;
  vm.get = get;

  return {
    get: vm.get
  };

  function get(id, cb) {
    vm.cb = cb;
    Getter.get('http://groupleapp.herokuapp.com/api/event/profile/' + id, callback);
  }

  function callback(data) {
    //middleware for profile callback
    data.data = data.data[0];
    return vm.cb(data);
  }
}

module.exports = EventProfileGetter;
