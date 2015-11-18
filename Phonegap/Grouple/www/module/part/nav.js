//simple jquery to slide down a menu
$(document).ready(function()
{
    var storage = window.localStorage;
    var ids =
    ["#nav-home", "#nav-friends", "#nav-groups",
        "#nav-profile", "#nav-events",
        "#nav-messages", "#nav-logout"];
    //looping to apply on clicks
    ids.forEach(function(id)
    {
      $(id).click(function()
        {
            if ($("div#nav-menu ul").hasClass("expanded"))
            {
                $("div#nav-menu ul.expanded").removeClass("expanded").slideUp(250);
                $(this).removeClass("open");
                //$(".top-bar #expand").text("+");
            }
            if (id === ids[6]) 
                logout();
        });  
    });
    function logout()
    { //function to handling clearing memory and logging out user
      alert('Later ' + storage.getItem('first') + '!');
      //$state.go('login');
      location.href = '#login';
      storage.clear(); //clear storage
    };
    //TODO: work on setting title from outside controllers
    /*vm.$on('setTitle', function(args)
    {
        alert('emit made it to $on');
    });*/
            
});