'use strict'
module.exports = function(ListFetcher)
{ //friend invite directive
    return {
      restrict: 'E',
      templateUrl: "module/adder/group-create/friend-invite.html",
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
          alert("here in inv ite");
        }; //end invite
      }, //end friend invite list controller
      controllerAs: "friendInvite"
    };
}; //end friend invite directive