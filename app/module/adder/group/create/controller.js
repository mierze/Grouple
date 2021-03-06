'use strict'
function GroupCreateController($state, Creator) {
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.post.name = '';
  vm.post.about = '';
  vm.post.pub = '0';
  vm.post.location = '';
  vm.create = create;
  vm.showErrors = showErrors;
  
  //functions
  function create() {
    //create function
    //set creator to the current user
    vm.post.creator = storage.getItem('email');
    //public validation
    if (!(vm.post.pub === '0' || vm.post.pub === '1')) {
      //public is not set, have some fancy red text pop up
      alert('Please ensure public or private is selected!');
    }
    alert('Before creator call:\n' + JSON.stringify(vm.post));
    Creator.create(vm.post, 'group', function(data)
    { //creater create
      alert('Group create returns -> ' + JSON.stringify(data));
      if (data['success'] > 0) { //created group successfully
        //TODO: find out if creator is added
        $state.go('group-invite', {id: data['id']});
      }
    });
  }
  function showErrors() {
    alert('You done darn did it!\nPlease fill out this form correctly.')
  }
} //end group create controller

module.exports = GroupCreateController;