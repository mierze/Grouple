'use strict'
function FriendsController($rootScope, FriendInviter) {
    var vm = this;
    var storage = window.localStorage;
    $rootScope.$broadcast('setTitle', 'Friends');
    vm.post = {};
    vm.showAddFriend = false;
    vm.post.from = storage.getItem('email');
    vm.send = send;
    vm.toggleAddFriend = toggleAddFriend;

    //functions
    function send() {
        alert(JSON.stringify(vm.post));
        FriendInviter.send(vm.post, function callback(data) {
            alert(JSON.stringify(data));
        });
    }

    function toggleAddFriend() {
        if (vm.showAddFriend)
            vm.showAddFriend = false;
        else
            vm.showAddFriend = true;
    }
}
module.exports = FriendsController;
