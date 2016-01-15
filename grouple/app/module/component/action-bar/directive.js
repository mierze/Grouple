'use strict'
function ActionBarDirective($state, $rootScope) {
    return {
        restrict: 'E',
        templateUrl: 'module/component/action-bar/layout.html',
        controller: actionBarCtrl,
        controllerAs: 'vm' //or actionBarCtrl
    };
    
    function actionBarCtrl($scope, $filter) {
        var vm = this;
        var storage = window.localStorage;
        vm.title = 'Grouple';
        vm.showNav = false;
        vm.logout = logout;
        vm.toggleNav = toggleNav;
        vm.back = back;
        
        $scope.$on('setTitle', function(event, data) {
            vm.title = $filter('limitTo')(data, 16, 0);
            $scope.$emit('showActionBar', true); //for now
        });
        
        $scope.$on('showActionBar', function(event, show) {
          vm.showActionBar = show;
        });
        
        //functions
        function toggleNav() {
            //broadcast a message to display the menu from rootScope
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
        function logout() {
            //function to handling clearing memory and logging out user
            alert('Later ' + storage.getItem('first') + '!');
            //$state.go('login');
            //location.href = '#login';
            storage.clear(); //clear storage
        } //end logout
    } //end action bar controller
} //end action bar directive

module.exports = ActionBarDirective;

