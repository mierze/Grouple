//simple jquery to slide down a menu
$(document).ready(function()
{$("#nav-open").click(function()
    {
        if ($("div#nav-menu ul").hasClass("expanded")) {
            $("div#nav-menu ul.expanded").removeClass("expanded").slideUp(250);
            $(this).removeClass("open");
            //$(".top-bar #expand").text("+");
        } else {
            $("div#nav-menu ul").addClass("expanded").slideDown(250);
            $(this).addClass("open");
           // $(".top-bar #expand").text("-");
        }
    });
    //PANDA code extra clicks to disable it
    var home = ["#home", "#nav-home"];
    var friends =  ["#friends", "#nav-friends"];
    var groups = ["#groups", "#nav-groups"];
    var profile = ["#user-profile", "#nav-profile"];
    var messages = ["#contacts", "#nav-messages"];

    if ($("#home").is(':visible'))
    {
        $("#nav-home").click(function()
        {
            alert("WORKED");
            if ($("div#nav-menu ul").hasClass("expanded")) {
                $("div#nav-menu ul.expanded").removeClass("expanded").slideUp(250);
                $(this).removeClass("open");
                //$(".top-bar #expand").text("+");
            } else {
                $("div#nav-menu ul").addClass("expanded").slideDown(250);
                $(this).addClass("open");
               // $(".top-bar #expand").text("-");
            }
    });
        }
    
        });