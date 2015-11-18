'use strict'
module.exports = function($stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  var vm = this;
  var storage = window.localStorage;
  vm.showEdit = false;
  vm.privs = {};
  vm.privs.mod = true;
  vm.privs.admin = true;//TODO set this
  //TODO : return role in get_group_info of 'user' and then show editable stuff depending
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init()
  { //start init function
    var type = 'group';
    vm.post = {};
    //case that id is for logged user's email
    if($stateParams.id !== null)
      vm.post.id = $stateParams.id;
    else
     alert('Invalid group ID, please go back and try again.');
    vm.post.user = storage.getItem('email');
    ProfileFetcher.fetch(vm.post, type, function(data)
    { //start fetch profile
      alert(data['message']);
      if (data['success'] === 1)
      {
        //PANDA set for now. next get from api
        vm.editable = true;
        vm.info = data['info'];
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    vm.post.content = type; //TODO this is ugly
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
  //end modal functionality
}; //end profile controller