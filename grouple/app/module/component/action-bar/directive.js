'use strict'
function ActionBarDirective($rootScope, $state) {
    return {
        restrict: 'E',
        templateUrl: 'module/component/action-bar/layout.html',
        controller: actionBarCtrl,
        controllerAs: 'actionBarCtrl' //or actionBarCtrl
    };
    
    function actionBarCtrl($scope, $filter) {
        var vm = this;
        var storage = window.localStorage;
        vm.title = 'Grouple';
        vm.toggleNav = toggleNav;
        vm.back = back;
        
        $scope.$on('setTitle', function setTitle(event, data) {
            vm.title = $filter('limitTo')(data, 16, 0);
        });
        
        //functions
        function toggleNav() {
            $rootScope.$broadcast('showNavMenu');    
        }
        function back() {
            //TODO:
            //when back would repopulate a invite or something similar
                //go back to something else
            //possibly: if history.back is same address
                //go back again
            history.back();
        } //end back
    } //end action bar controller
} //end action bar directive

module.exports = ActionBarDirective;