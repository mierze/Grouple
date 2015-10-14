'use strict'
module.exports = function($state)
{ //badge item directive
  return {
    restrict: 'E',
    templateUrl: "module/list/badge/badge-item.html",
    controller: function()
    {
      this.image = "unknown";
      this.init = function(item)
      { //init function
        if (item.level > 0)
        {
          this.lower = item.name.toLowerCase();
          this.image = this.lower.replace(" ", "-");
        }       
        //LOGIC:
        //Health Nut L2 becomes
          //resource/image/badge/health-nut_ + level + '_' + (male||female) + .png
      }; //end init function
      this.zoom = function()
      { //zoom function
        alert("HERE IN ZOOM FUNCTION!");
        //$state.go('badge', {id: id});
      }; //end zoom function
    },
    controllerAs: "badge"
  };
}; //end badge item directive
