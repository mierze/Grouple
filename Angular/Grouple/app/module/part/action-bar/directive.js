'use strict'
module.exports = function($state)
{   //TODO:
    //Disable backing up into crucial areas and to login / register screen
    return {
        restrict: 'E',
        templateUrl: 'module/part/action-bar/layout.html',
        controller: function($scope, $filter, $state)
        {
            //TODO: remove jquery and just use angular
            var vm = this;
            var storage = window.localStorage;
            
            
            $scope.$on('setTitle', function(event, data)
            {
                $('#nav-title').text($filter('limitTo')(data, 16, 0));
            });
            
            $scope.$on('hideActionBar', function(event, data)
            {
              vm.hideActionBar = true; 
            });
    
            $("#nav-open").click(openNav);
            //vm.logout = logout;
            $('#nav-back').on('click', function()
            {
                //TODO:
                //when back would repopulate a invite or something similar
                    //go back to something else
                //possibly: if history.back is same address
                    //go back again
                history.back();
            });
            
            //functions
            function openNav()
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
            };
        }
    };
};

