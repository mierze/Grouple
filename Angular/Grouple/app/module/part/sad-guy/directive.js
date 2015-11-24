'use strict'
module.exports = function()
{ //sad guy directive
    return {
        restrict: 'E',
        templateUrl: 'module/part/sad-guy/layout.html',
        controller: function()
        {
            var vm = this;
            vm.caption = 'Sorry.';
            vm.setCaption = setCaption;
            
            //functions
            function setCaption(caption)
            {
                vm.caption = caption;
            }
        },
        controllerAs: 'sadGuyCtrl'
    };
}; //end sad guy directive

