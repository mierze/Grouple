(function() //wrap
{
  //PANDA: look at liquids and think of a good way to store sessions
  var storage = window.localStorage;
  //CHANGING SCREENS -> ui-sref="home" or in controller $state.go('home')
  
  /*********************************************
  *************** MAIN MODULE FUNCTIONS BELOW ***************
  *********************************************/
  //function to get parameters from url, takes in key and returns value if exists, or null
  this.getVal = function(key)
  {
    var result = null;
    window.location.search.substr(1).split("&").forEach(function (item)
    {
      var keySet = item.split("=");
      if (keySet[0] === key)
      {
        result = keySet[1];
      }
    });
    return result;
  }
  
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
    document.location.href="#login";
    alert("Later playa!");
  };
})(); //end wrap
