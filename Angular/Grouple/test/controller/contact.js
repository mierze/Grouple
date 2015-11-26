'use strict'
describe('Controller: Contact', function()
{
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  beforeEach(inject(function(_$controller_, $q)
  {// The injector unwraps the underscores (_) from around the parameter names when matching
    $controller = _$controller_;
    deferred = $q.defer;
    controller = $controller('ContactController', {});
  }));

  describe('Variables', function()
  {
    it('should have post, post.user, post.email instantiated', function()
    {
      expect(controller.post).toBeDefined();
      expect(controller.post.user).toBeDefined();
      expect(controller.post.email).toBeDefined();
    }); 
  });
});