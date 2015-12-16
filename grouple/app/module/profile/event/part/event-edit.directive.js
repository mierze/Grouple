'use strict'
function EventEditDirective($http, $filter) {
  //event edit directive
  var storage = window.localStorage; //grab local storage
  return {
    restrict: 'E',
    templateUrl: 'module/profile/event/part/event-edit.html',
    controller: function() {
      var vm = this;
      vm.save = function(info)
      {
        //TODO: use promsises and external service
        info.startDate = $filter('date')(info.startDate, 'yyyy-MM-dd hh:mm:ss');
        info.endDate = $filter('date')(info.endDate, 'yyyy-MM-dd hh:mm:ss');
        var url = 'http://mierze.gear.host/grouple/api/update_event.php';
        alert(JSON.stringify(info));
        //http request to fetch list from server PANDA refactor out this
        $http({
          method : 'POST',
          url : url,
          data : info
        }).success(function(data)
        {
          if (data['success'] === 1) {
            alert(data['message']);
          }
          else if (data['success'] === 0) {
            //PANDA, populate sad guy.
            alert(data['message']);
          }
          else {
            //generic catch
            alert(data['message']);
            alert('Error updating event profile.');
          }
        })
        .error(function(data) {
          alert('Error contacting server.');
        });
    };
  },
  controllerAs: 'eventEditCtrl'
  };
}; //end event edit directive

module.exports = EventEditDirective;
