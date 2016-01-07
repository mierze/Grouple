'use strict'
function EventListController($rootScope, $stateParams, EventListGetter) {
  var vm = this;
  var storage = window.localStorage;
  vm.content = $stateParams.content;
  
  setTitle();
  
  //todo, prepare for group-events
  if ($stateParams.id != null)
    vm.email = $stateParams.id;
  else
    vm.email = storage.getItem('email');
  
  getEvents(); //get list

  //functions
  function setTitle() {
    var title;
    if (vm.content === 'invites')
      title = 'Event Invites';
    else if (vm.content === 'pending')
      title = 'Pending Events';
    else if (vm.content === 'upcoming')
      title = 'Upcoming Events';
    else if (vm.content === 'past')
      title = 'Past Events';
    else if (vm.content === 'declined') 
      title = 'Declined Events'
    else //catch
      title = 'Events';
    //set title  
    $rootScope.$broadcast('setTitle', title);
  }
  function getEvents() {
    EventListGetter.get(vm.email, vm.content, function setEvents(data) {
      if (data['success'] === 1)
        vm.items = data['data'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    });
  } //end get events
} //end event list controller

module.exports = EventListController;
