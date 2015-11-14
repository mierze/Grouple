'use strict'
module.exports = function($state)
{   //TODO:
    //Disable backing up into crucial areas and to login / register screen
    return {
        restrict: 'E',
        templateUrl: 'module/part/action-bar.html',
        controller: function($scope, $state)
        {
            var storage = window.localStorage;
            $('#nav-back').on('click', function()
            {
                //TODO:
                //when back would repopulate a invite or something similar
                    //go back to something else
                //possibly: if history.back is same address
                    //go back again
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
              alert('Later ' + storage.getItem('first') + '!');
              $state.go('login');
              storage.clear(); //clear storage
            };
            //TODO: work on setting title from outside controllers
            $scope.$on('setTitle', function(args)
            {
                alert('emit made it to $on');
            });
        }
    };
};

