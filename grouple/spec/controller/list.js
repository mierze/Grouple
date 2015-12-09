'use strict'
describe('Controller: List', function()
{
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  var stateParams = { content: 'groups' };
  beforeEach(inject(function(_$controller_, $q)
  {
    $controller = _$controller_;
    deferred = $q.defer;
    controller = $controller('ListController', {$stateParams: stateParams});
  }));

  //TODO: test each stateParams input and fetch to correct service
  describe('Variables', function()
  {
    it('should have post and post.user instantiated', function()
    {
      //controller.$stateParams.content = 'groups';
      expect(controller.post).toBeDefined();
      expect(controller.post.user).toBeDefined();
    }); 
  });
});