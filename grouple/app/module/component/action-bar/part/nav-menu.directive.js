'use strict'
function NavMenuDirective($state) {
    return {
        restrict: 'E',
        templateUrl: 'module/component/action-bar/part/nav-menu.html',
        controller: navMenuCtrl,
        controllerAs: 'navMenuCtrl' //or actionBarCtrl
    };
    
    function navMenuCtrl($scope, $filter, $state) {
        var vm = this;
        var storage = window.localStorage;
        vm.logout = logout;
        $scope.$on('showNavMenu', function(event, data) {
            vm.showNavMenu = vm.showNavMenu ? false : true;
        });
        function logout() {
            //function to handling clearing memory and logging out user
            alert('Later ' + storage.getItem('first') + '!');
            //$state.go('login');
            storage.clear(); //clear storage
        } //end logout
    } //end action bar controller
} //end action bar directive

module.exports = NavMenuDirective;

