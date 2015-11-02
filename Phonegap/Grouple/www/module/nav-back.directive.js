'use strict'
module.exports = function($state)
{
    //PANDA:
    //Make bool for displaying back / menu
    //Make this directive for entire nav bar
    //Disable backing up into crucial areas and to login / register screen
    //On register / logout dont display menu and back button
    return {
        restrict: 'E',
        template: '<a id="nav-back"></a>',
        link: function(scope, element, attrs) {
            $(element).on('click', function() {
                    
                //alert(element);
                history.back();
                $apply();
            });
        }
    };
};

