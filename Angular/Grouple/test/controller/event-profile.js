'use strict'
describe('Controller: Event Profile', function()
{
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  beforeEach(inject(function(_$controller_, $q)
  {
    $controller = _$controller_;
    deferred = $q.defer;
    controller = $controller('EventProfileController', {});
  }));

  describe('Functions', function()
  {
    it('should have init, toggleEdit set', function()
    {
      expect(controller.init).toBeDefined();
      expect(controller.toggleEdit).toBeDefined();
    }); 
  });
  describe('Variables', function()
  {
    it('should have post and post.last instantiated', function()
    {
      expect(controller.showEdit).toBeDefined();
      expect(controller.privs).toBeDefined();
      expect(controller.privs.admin).toBeDefined();
      expect(controller.privs.mod).toBeDefined();
     // controller.init();
      //expect(controller.post).toBeDefined();
    }); 
  });
});