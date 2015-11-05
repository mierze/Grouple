'use strict'
module.exports = function($state)
{
    //TODO:
    //Make bool for displaying back / menu
    //Disable backing up into crucial areas and to login / register screen
    //On register / logout dont display menu and back button
    return {
        restrict: 'E',
        templateUrl: 'part/action-bar.html',
        controller: function($scope) {
            $('#nav-back').on('click', function()
            {
                history.back();
            });
            $("#nav-open").click(function()
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
        }
    };
};

