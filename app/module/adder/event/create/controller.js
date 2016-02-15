'use strict'
function EventCreateController($filter, $state, Creator) {
  var vm = this;
  var storage = window.localStorage;
  vm.post = {};
  vm.created = false; //boolean whether event has been created
  vm.create = create;
  vn.showErrors = showErrors;
  
  //functions
  function create() {
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
    Creator.create(vm.post, 'event', function(data) {
      alert(data['message']);
      if (data['success'] === '1') 
        //TODO: give user option to go to profile or invite groups
        $state.go('event-invite', {id: data['id']});
    }); //end create
  }
  function showErrors() {
    alert('Uh, or. An error occured creating this event. Please try again.');
  }
} //end event create controller

module.exports = EventCreateController;