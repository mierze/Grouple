'use strict'
function EventProfileController($rootScope, $stateParams, $state, EventProfileGetter, EventImageGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.type = 'event';
  vm.privs = {};
  vm.privs.admin = true;
  vm.privs.mod = true;
  vm.showEdit = false;
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init() {
    if($stateParams.id !== null) {
      vm.id = $stateParams.id;
      getProfile();
    }
    else
      alert('ID not specified.');
    getProfile();
  } //end init
  function toggleEdit() {
    if (vm.showEdit) 
      vm.showEdit = false;
    else
      vm.showEdit = true;
  } //end toggle edit
  function getProfile() {
    EventProfileGetter.get(vm.id, function setProfile(data) {
      if (data['success'] === 1) {
        vm.editable = true;
        vm.info = data.data;
        $rootScope.$broadcast('setTitle', vm.info.name);
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    EventImageGetter.get(vm.id, function setImage(data) {
      if (data['success'] === 1) {
        var imgUrl = 'data:image/png;base64,' + data['image'];
        vm.image = imgUrl;
      }
      else {
        //generic catch
        vm.imageNull = true;
        alert(data['message']);
      }
    });
  } //end get profile
} //end event profile controller

module.exports = EventProfileController;