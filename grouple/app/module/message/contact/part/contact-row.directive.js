'use strict'
function ContactRowDirective($state) {
    return {
        restrict: 'E',
        templateUrl: 'module/message/contact/part/contact-row.html',
        controller: contactRowCtrl,
        controllerAs: 'contactRowCtrl'
    };
    
    function contactRowCtrl() {
        var vm = this;
        vm.imgEnc = imgEnc;
        
        //functions
        function imgEnc(image) {
          return 'data:image/png;base64,' + image;
        }
    } //end contact row controller
} //end contact row directive

module.exports = ContactRowDirective;