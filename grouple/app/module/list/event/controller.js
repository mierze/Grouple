'use strict'
function EventListController($rootScope, $stateParams, ListFetcher) {
  //event list controller
  var vm = this;
  var storage = window.localStorage;
  var params = {};
  
  setTitle($stateParams.content);
  
  //prepare parameters
  if ($stateParams.id != null)
    params.id = $stateParams.id;
  else
    params.id = storage.getItem('email');
  params.user = storage.getItem('email');
  
  fetchList(); //get list

  //functions
  function setTitle(content) {
    var title;
    if (content === 'invites')
      title = 'Event Invites';
    else if (content === 'pending')
      title = 'Pending Events';
    else if (content === 'upcoming')
      title = 'Upcoming Events';
    else if (content === 'past')
      title = 'Past Events';
    else //catch
      title = 'Events';
    //set title  
    $rootScope.$broadcast('setTitle', title);
  };
  function fetchList() {
    ListFetcher.fetch(params, $stateParams.content, function(data) {
      //start fetch list
      if (data['success'] === 1)
        vm.items = data['data'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end fetch list
  };
}; //end event list controller

module.exports = EventListController;
