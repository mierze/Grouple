'use strict'
function UserGetter(Getter) {
  var vm = this;
  vm.get = get;

  return {
    get: vm.get
  };

  function get(email, cb) {
    vm.cb = cb;
    Getter.get('http://localhost:1337/api/user/profile/' + email, callback);
  }

  function callback(data) {
    //middleware for callback
    data.data = data.data[0];
    return vm.cb(data);
  }
}

module.exports = UserGetter;
