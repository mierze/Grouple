'use strict'
module.exports = function($state)
{
    return {
        restrict: 'E',
        templateUrl: 'module/part/action-bar/layout.html',
        controller: function($scope, $filter, $state)
        {
            var vm = this;
            var storage = window.localStorage;
            vm.title = 'Grouple';
            vm.logout = logout;
            vm.back = back;
            
            $scope.$on('setTitle', function(event, data)
            {
                vm.title = $filter('limitTo')(data, 16, 0);
            });
            
            $scope.$on('showActionBar', function(event, data)
            {
              vm.showActionBar = true;
            });
            
            //functions
            function toggleNav()
            {
                alert('calling');
              if (vm.showNav)
                vm.showNav = false;
              else
              {
                vm.showNav = true;
              
              alert('showing it now');
              }
            }
            function back()
            {
                //TODO:
                //when back would repopulate a invite or something similar
                    //go back to something else
                //possibly: if history.back is same address
                    //go back again
                history.back();
            };
            function logout()
            { //function to handling clearing memory and logging out user
              alert('Later ' + storage.getItem('first') + '!');
              //$state.go('login');
              //location.href = '#login';
              storage.clear(); //clear storage
            };
        },
        controllerAs: 'vm'
    };
};

