'use strict'
function GroupProfile(Getter) {
  var vm = this;
  vm.get = get;
  
  return {
    get: vm.get
  };
  
  function get(id, cb) {
    vm.cb = cb;
    Getter.get('https://groupleapp.herokuapp.com/api/group/profile/' + id, callback);
  }
  
  function callback(data) {
    //middlware for data
    data.data = data.data[0];
    return vm.cb(data);
  }
}

module.exports = GroupProfile;