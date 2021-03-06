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
        vm.logged = true;

        $scope.$on('setLogged', function setLogged(event, data) {
            vm.logged = data;
            storage.setItem('logged', vm.logged);
        });

        $scope.$on('setTitle', function setTitle(event, data) {
            vm.title = $filter('limitTo')(data, 16, 0);
        });

        $scope.$on('showNavMenu', function(event) {
            vm.showNavMenu = vm.showNavMenu ? false : true;
        });

        function logout() {
            //function to handling clearing memory and logging out user
            alert('Later ' + storage.getItem('first') + '!');
            storage.clear(); //clear storage
            vm.logged = false;
            $state.go('login');
        } //end logout
    } //end action bar controller
} //end action bar directive

module.exports = NavMenuDirective;
