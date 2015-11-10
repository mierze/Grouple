//simple jquery to slide down a menu
$(document).ready(function()
{
    var ids =
    ["#nav-home", "#nav-friends", "#nav-groups",
        "#nav-profile", "#nav-events",
        "#nav-messages", "#nav-logout"];
    //looping to apply on clicks
    ids.forEach(function(id)
    {
      $(id).click(function()
        {
            if ($("div#nav-menu ul").hasClass("expanded")) {
                $("div#nav-menu ul.expanded").removeClass("expanded").slideUp(250);
                $(this).removeClass("open");
                //$(".top-bar #expand").text("+");
            }
            else
            {
                $("div#nav-menu ul").addClass("expanded").slideDown(250);
                $(this).addClass("open");
               // $(".top-bar #expand").text("-");
            }
        });  
    });

});