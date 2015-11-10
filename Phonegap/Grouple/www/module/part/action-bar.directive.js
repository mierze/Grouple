'use strict'
module.exports = function($state)
{
    //TODO:
    //Make bool for displaying back / menu
    //Disable backing up into crucial areas and to login / register screen
    //On register / logout dont display menu and back button
    return {
        restrict: 'E',
        templateUrl: 'module/part/action-bar.html',
        controller: function($scope, $state)
        {
            var storage = window.localStorage;
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
            //function to handling clearing memory and logging out user
            $scope.logout = function()
            {
              storage.clear(); //clear storage
              $state.go('login');
              alert('Later ' + storage.getItem('name') + '!');
            };
            //TODO: work on setting title from outside controllers
            $scope.$on('setTitle', function(args)
            {
                alert('emit made it to $on');
            });
        }
    };
};

