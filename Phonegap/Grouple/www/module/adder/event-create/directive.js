'use strict'
module.exports =  function(ListFetcher)
{ //group invite directive
  var storage = window.localStorage;
  //group invite list
  return {
    restrict: 'E',
    templateUrl: "module/adder/event-create/group-invite.html",
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
    controllerAs: "groupInvite"
    };
}; //end group invite directive