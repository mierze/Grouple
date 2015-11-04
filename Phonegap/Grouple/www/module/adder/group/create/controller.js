'use strict'
module.exports = function($scope, $state, Creator)
{ //group create controller
  var storage = window.localStorage;
  //init post parameters
  $scope.post = {};
  $scope.post.name = '';
  $scope.post.about = '';
  $scope.post.pub = '0';
  $scope.post.location = '';
  $scope.create = function()
  { //create function
    //set creator to the current user
    $scope.post.creator = storage.getItem('email');
    //public validation
    if (!($scope.post.pub === '0' || $scope.post.pub === '1'))
    {
      //public is not set, have some fancy red text pop up
      alert('Please ensure public or private is selected!');
    }
    alert('Before creator call:\n' + JSON.stringify($scope.post));
    Creator.create($scope.post, 'group', function(data)
    { //creater create
      alert('Group create returns -> ' + JSON.stringify(data));
      if (data['success'] > 0)
      { //created group successfully
        //TODO: find out if creator is added
        $state.go('group-invite', {id: data['id']});
      }
    });
  };
  $scope.showErrors = function()
  {
    alert('You done darn did it!\nPlease fill out this form correctly.')
  };
}; //end group create controller