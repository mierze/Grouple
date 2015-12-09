'use strict'
module.exports = function($rootScope, $stateParams, ListFetcher)
{ //event list controller
  var vm = this,
  storage = window.localStorage,
  params = {};
  
  setTitle($stateParams.content);
  
  //prepare parameters
  if ($stateParams.id != null)
    params.id = $stateParams.id;
  else
    params.id = storage.getItem('email');
  params.user = storage.getItem('email');
  
  fetchList(); //get list

  //functions
  function setTitle(content)
  {
    var title;
    if (content === 'event_invites')
      title = 'Event Invites';
    else if (content === 'events_pending')
      title = 'Pending Events';
    else if (content === 'events_upcoming')
      title = 'Upcoming Events';
    else if (content === 'events_past')
      title = 'Past Events';
    else //catch
      title = 'Events';
    //set title  
    $rootScope.$broadcast('setTitle', title);
  };
  function fetchList()
  {
    ListFetcher.fetch(params, $stateParams.content, function(data)
    { //start fetch list
      if (data['success'] === 1)
        vm.items = data['items'];
      else if (data['success'] === 0)
        vm.sadGuy = true;
      else
        alert(data['message']);
    }); //end fetch list
  };
}; //end event list controller
