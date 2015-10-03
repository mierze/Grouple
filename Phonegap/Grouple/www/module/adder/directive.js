(function()
{ //create module directives
  var storage = window.localStorage;
  angular.module('adder')
  .directive("friendInviteList", function(ListFetcher) {
    return {
      restrict: 'E',
      templateUrl: "module/adder/layout/partial/friend-invite-list.html",
      controller: function()
      { //start friend invite list controller
        this.post = {};
        this.post.email = storage.getItem("email");
        this.post.user = this.post.email;
        this.users = {};
        ListFetcher.fetch(this.post, 'friends', function(data)
        {                      
          alert(data["message"]);
          if (data["success"])
          {
            this.users = data["users"];
          }
        });
        this.invite = function()
        { //start invite
          alert("here in invite");
        }; //end invite
      }, //end friend invite list controller
      controllerAs: "friendInvite"
    };
  }) //end user row directive
  
  //group row directive
  .directive("groupInviteList", function(ListFetcher) {
  return {
    restrict: 'E',
    templateUrl: "module/adder/layout/partial/group-invite-list.html",
    controller: function()
    {
      this.post = {};
      this.post.email = storage.getItem("email");
      this.post.user = this.post.email;
      this.groups = {};
      ListFetcher.fetch(this.post, 'groups', function(data)
      {                      
        alert(data["message"]);
        if (data["success"])
        {
          this.groups = data["groups"];
        }
      });
      this.invite = function()
      { //start invite
        alert("here in invite");
      }; //end invite
    },
    controllerAs: "group"
    };
  }) //end group row directive
})();