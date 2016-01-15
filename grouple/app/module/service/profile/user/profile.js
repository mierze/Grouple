'use strict'
function Profile(Getter) {
  var vm = this;
  vm.get = get;
  
  return {
    get: vm.get
  };
  
  function get(email, cb) {
    vm.cb = cb;
    Getter.get('https://groupleapp.herokuapp.com/api/user/profile/' + email, callback);
  }
  
  function callback(data) {
    //middleware for callback
    alert(JSON.stringify(data));
    data.data = data.data[0];
    //alert(JSON.stringify(data.data[0]));
    return vm.cb(data);
  }
}

module.exports = Profile;