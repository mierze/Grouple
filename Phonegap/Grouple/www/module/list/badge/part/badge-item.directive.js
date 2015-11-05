'use strict'
module.exports = function($state)
{ //badge item directive
  return {
    restrict: 'E',
    templateUrl: 'module/list/badge/part/badge-item.html',
    controller: function()
    {
      this.image = 'unknown';
      this.init = function(item)
      { //init function
        if (item.level > 0)
        { //setting image source
          this.lower = item.name.toLowerCase();
          this.image = this.lower.replace(' ', '-');
        }
      }; //end init function
      this.zoom = function()
      { //zoom function
        alert('HERE IN ZOOM FUNCTION!');
        //would be ideal to have generic modal overlay
        //in this function inject the item info into the modal
        
        //alternatively:
        //$state.go('badge', {id: id});
      }; //end zoom function
    },
    controllerAs: 'badgeItemCtrl'
  };
}; //end badge item directive
