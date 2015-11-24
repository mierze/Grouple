'use strict'
module.exports = function($filter, $state, Creator)
{ //event create controller
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.created = false; //boolean whether event has been created
  vm.create = create;

  //functions
  function create()
  { //create function
    //form validation
    alert('Before creator service:\n'+JSON.stringify(vm.post));
    vm.post.recurring = 0;
    vm.post.creator = storage.getItem('email');
    if (vm.info.minPart == null)
      vm.info.minPart = 1;
    if (vm.info.maxPart == null)
      vm.info.maxPart = 0;
    if (vm.info.recType) {
      //code
    }
    //TODO: figure out these dates
    vm.info.startDate = $filter('date')(vm.info.startDate, 'yyyy-MM-dd hh:mm:ss');
    vm.info.endDate = $filter('date')(vm.info.endDate, 'yyyy-MM-dd hh:mm:ss'); 
    Creator.create(vm.post, 'event', function(data)
    { //creater create
      alert(data['message']);
      if (data['success'] === '1') 
      //TODO: give user option to go to profile or invite groups
        $state.go('event-invite', {id: data['id']});e
    }); //end creater create
  };
}; //end event create controller