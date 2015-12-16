'use strict'
function EventProfileController($rootScope, $stateParams, $state, EventProfileFetcher, ImageFetcher) {
  //event profile controller
  var vm = this;
  var type = 'event'; //type of profile
  var storage = window.localStorage;
  var params = {};
  vm.privs = {};
  //TODO: fetch privs
  vm.privs.admin = true;
  vm.privs.mod = true;
  vm.showEdit = false;
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init() {
    //start init function
    if($stateParams.id !== null)
      params.id = $stateParams.id;
    else
     alert('ID not specified.');
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
    EventProfileFetcher.fetch(params, function(data) {
      //start fetch profile
      if (data['success'] === 1) {
        vm.editable = true;
        vm.info = data['data'];
        $rootScope.$broadcast('setTitle', vm.info.name);
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    params.content = type;
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
}; //end event profile controller

module.exports = EventProfileController;