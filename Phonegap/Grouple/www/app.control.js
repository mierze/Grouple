(function()
{ //wrap
  //PANDA: look at liquids and think of a good way to store sessions
  var storage = window.localStorage;
  
  angular.module('grouple')
  .controller('navigation', function($scope, $state)
  {
    $scope.navigate = function(location)
    {
      //alert("attempt to go");
      $state.go(location);
    }
  });
  /*********************************************
  *************** MAIN MODULE FUNCTIONS BELOW ***************
  *********************************************/
  
  //modal visibility toggle functions below
  this.showAddFriend = function()
  {
    document.getElementById('addfriend-modal').style.display = 'block';
  };
  this.closeAddFriend = function()
  {
    document.getElementById('addfriend-modal').style.display = 'none';
  };
  this.showEditProfile = function()
  {
    document.getElementById('editprofile-modal').style.display = 'block';
  };
 this.closeEditProfile = function()
  {
    document.getElementById('editprofile-modal').style.display = 'none';
  };
  this.showEventCreate = function()
  {
    document.getElementById('event-create').style.display = 'block';
  };
  this.closeEventCreate = function()
  {
    document.getElementById('event-create').style.display = 'none';
  };
  
  //function to handling clearing memory and logging out user
  this.logout = function()
  {
    storage.clear(); //clear storage
    alert(storage.getItem("email"));
    document.location.href="#login";
    alert("Later playa!");
  };
})(); //end wrap
