(function() {
  //create module directives
  var storage = window.localStorage; //grab local storage
 
  angular.module('directive')
  //edit user profile directive
  .directive("eventCreate", function() {
  return {
    restrict: 'E',
    templateUrl: "partial/event-create.html",
    controller: function()
    {
      this.create = function()
      {
        alert("SAVE HERE");
      };
    },
    controllerAs: "eventCreate"
    };
  }) //end edit user profile directive
})();