'use strict'
module.exports = function($stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  var vm = this;
  var storage = window.localStorage;
  vm.privs = {};
  //TODO: fetch privs
  vm.privs.admin = true;
  vm.privs.mod = true;
  vm.showEdit = false;
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init()
  { //start init function
    var type = 'event'; //type of profile
    vm.post = {};
    //case that id is for logged user's email
  
    if($stateParams.id !== null)
      vm.post.id = $stateParams.id;
    else
     alert('problem with id passed');
    vm.post.user = storage.getItem('email');
    ProfileFetcher.fetch(vm.post, type, function(data)
    { //start fetch profile
      if (data['success'])
      {
        //PANDA set for now. next get from api
        vm.editable = true;
        vm.info = data['info'];
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    vm.post.content = type;
    ImageFetcher.fetch(vm.post, type, function(data)
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
  }; //end init function
  function toggleEdit()
  {
    if (vm.showEdit) 
      vm.showEdit = false;
    else
      vm.showEdit = true;
  };
}; //end profile controller