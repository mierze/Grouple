'use strict'
function GroupProfileController($rootScope, $stateParams, $state, ProfileFetcher, ImageFetcher) {
  //group profile controller
  var vm = this;
  var storage = window.localStorage;
  var type = 'group';
  var params = {};
   
  vm.showEdit = false;
  vm.privs = {};
  vm.privs.mod = true;
  vm.privs.admin = true;//TODO set this
  //TODO : return role in get_group_info of 'user' and then show editable stuff depending
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init() {
    //start init function
    if($stateParams.id !== null)
      params.id = $stateParams.id;
    else
     alert('Invalid group ID, please go back and try again.');
    params.user = storage.getItem('email');
    fetchProfile();
  }; //end init function
  function toggleEdit() {
    if (vm.showEdit)
      vm.showEdit = false;
    else
      vm.showEdit = true;
  };
  function fetchProfile() {
    ProfileFetcher.fetch(params, type, function(data) {
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
    params.content = type; //TODO this is ugly
    /*ImageFetcher.fetch(params, type, function(data)
    { //start fetch image
      if (data['success'] === 1)
      {
        var imgUrl = 'data:image/png;base64,' + data['image'];
        vm.image = imgUrl;
      }
      else
        //generic catch
        alert(data['message']);
    }); //end fetch image
    */
  };
}; //end group profile controller

module.exports = GroupProfileController;