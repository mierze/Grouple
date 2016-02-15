'use strict'
function EventEditDirective($state, $filter, EventEditer) {
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/event/part/event-edit.html',
    controller: eventEditCtrl,
    controllerAs: 'eventEditCtrl'
  };

  function eventEditCtrl() {
    var vm = this;
    vm.save = save;

    //functions
    function save(info) {
      info.startDate = $filter('date')(info.startDate, 'yyyy-MM-dd hh:mm:ss');
      info.endDate = $filter('date')(info.endDate, 'yyyy-MM-dd hh:mm:ss');
      alert(JSON.stringify(info));

      EventEditer.edit(info, type, function(data) {
        alert(data['message']);
        //if successful update ui and close out
        if (data["success"] === 1) {
          $state.go($state.current, {id: info.id}, {reload: true})
        }
      });
    }
  } //end event edit controller
} //end event edit directive

module.exports = EventEditDirective;
