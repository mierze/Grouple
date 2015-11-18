'use strict'
module.exports = function($state)
{ //badge item directive
  return {
    restrict: 'E',
    templateUrl: 'module/list/badge/part/badge-item.html',
    controller: function()
    {
      var vm = this;
      vm.image = 'unknown';
      vm.init = init;
      vm.zoom = zoom;
      
      //functions
      function init(item)
      { //init function
        if (item.level > 0)
        { //setting image source
          vm.lower = item.name.toLowerCase();
          vm.image = vm.lower.replace(' ', '-');
        }
      }; //end init function
      function zoom()
      { //zoom function
        alert('HERE IN ZOOM FUNCTION!');
        //would be ideal to have generic modal overlay
        //in vm function inject the item info into the modal
        
        //alternatively:
        //$state.go('badge', {id: id});
      }; //end zoom function
    },
    controllerAs: 'badgeItemCtrl'
  };
}; //end badge item directive
