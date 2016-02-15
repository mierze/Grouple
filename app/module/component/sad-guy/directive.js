'use strict'
function SadGuyDirective() {
    return {
        restrict: 'E',
        templateUrl: 'module/component/sad-guy/layout.html',
        controller: sadGuyCtrl,
        controllerAs: 'sadGuyCtrl'
    };
    function sadGuyCtrl() {
        var vm = this;
        vm.caption = 'Sorry, none to display.';
        vm.setCaption = setCaption;
        
        //functions
        function setCaption(caption) {
            vm.caption = caption;
        }
    } //end sad guy controller
} //end sad guy directive

module.exports = SadGuyDirective;