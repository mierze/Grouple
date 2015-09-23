(function()
{ //create module directives
  angular.module('list')
  .directive("userRow", function($state, InviteResponder) {
    return {
      restrict: 'E',
      templateUrl: "module/list/layout/partial/user-row.html",
      controller: function()
      {
        this.profile = function(id)
        {
          $state.go('user-profile', {id: id});
        };
        this.decision = function(post, decision)
        { //start decision
          this.type = decision + "_friend";
          InviteResponder.respond(post, this.type, function(data)
          {                      
            alert(data["message"]);
          });
        }; //end decision
      },
      controllerAs: "user"
    };
  }) //end user row directive
  
  //group row directive
  .directive("groupRow", function($state, InviteResponder) {
  return {
    restrict: 'E',
    templateUrl: "module/list/layout/partial/group-row.html",
    controller: function()
    {
      this.profile = function(id)
      {
        $state.go('group-profile', {id: id});
      };
      this.decision = function(post, decision)
      { //start decision
        this.type = decision + "_group";
        InviteResponder.respond(post, this.type, function(data)
        {                      
          alert(data["message"]);
        });
      }; //end decision
    },
    controllerAs: "group"
    };
  }) //end group row directive
  
  .directive("eventRow", function($state, InviteResponder)
  { //event row directive
    return {
      restrict: 'E',
      templateUrl: "module/list/layout/partial/event-row.html",
      controller: function()
      {
        this.profile = function(id)
        {
          $state.go('event-profile', {id: id});
        };
        this.decision = function(post, decision)
        { //start decision
          this.type = decision + "_event";
          InviteResponder.respond(post, this.type, function(data)
          {                      
            alert(data["message"]);
          });
        }; //end decision
      },
      controllerAs: "event"
    };
  }); //end event row directive
})();