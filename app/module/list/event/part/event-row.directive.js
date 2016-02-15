'use strict'
function EventRowDirective($state, InviteResponder) {
  return {
    restrict: 'E',
    templateUrl: 'module/list/event/part/event-row.html',
    controller: eventRowCtrl,
    controllerAs: 'eventRowCtrl'
  };
  
  function eventRowCtrl() {
    var vm = this;
    vm.profile = profile;
    vm.decision = decision;
    
    //functions
    function profile(id) {
      $state.go('event-profile', {id: id});
    }
    function decision(id, decision) {
      var post = {};
      post.id = id;
      post.user = storage.getItem('email');
      InviteResponder.respond(post, decision, 'event', function(data) {                      
        alert(data['message']);
        if (data['success'] === 1) {
          $state.go($state.current, {content: 'invites', id: storage.getItem('email')}, {reload: true});
        }
      });
    } //end decision
  } //end event row controller
} //end event row directive

module.exports = EventRowDirective;