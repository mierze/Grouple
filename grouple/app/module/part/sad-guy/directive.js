'use strict'
function SadGuyDirective() {
    //sad guy directive
    return {
        restrict: 'E',
        templateUrl: 'module/part/sad-guy/layout.html',
        controller: function() {
            var vm = this;
            vm.caption = 'Sorry, none to display.';
            vm.setCaption = setCaption;
            
            //functions
            function setCaption(caption) {
                vm.caption = caption;
            }
        },
        controllerAs: 'sadGuyCtrl'
    };
}; //end sad guy directive

module.exports = SadGuyDirective;