'use strict'
function GroupProfileController($rootScope, $stateParams, $state, GroupProfileGetter, GroupImageGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.showEdit = false;
  vm.privs = {};
  vm.privs.mod = true;
  vm.privs.admin = true;//TODO set this
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init() {
    if($stateParams.id !== null) {
      vm.id = $stateParams.id;
      getProfile();
    }
    else
     alert('Invalid group ID, please go back and try again.');
  } //end init
  function toggleEdit() {
    if (vm.showEdit)
      vm.showEdit = false;
    else
      vm.showEdit = true;
  }
  function getProfile() {
    GroupProfileGetter.get(vm.id, function setProfile(data) {
      //start fetch profile
      alert(data['message']);
      if (data['success'] === 1) {
        //TODO set for now. next get from api
        vm.editable = true;
        vm.info = data['data'];
        $rootScope.$broadcast('setTitle', vm.info.name);
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    /*GroupImageGetter.get(vm.id, function setImage(data) {
      if (data['success'] === 1) {
        var imgUrl = 'data:image/png;base64,' + data['image'];
        vm.image = imgUrl;
      }
      else {
        //generic catch
        vm.imageNull = true;
        alert(data['message']);
      }
    });*/
  } //end get profile
} //end group profile controller

module.exports = GroupProfileController;