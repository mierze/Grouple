'use strict'
describe('Controller: User Message', function()
{
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  beforeEach(inject(function(_$controller_, $q)
  {
	$controller = _$controller_;
	deferred = $q.defer;
	controller = $controller('UserMessageController', {});
  }));

  describe('Functions', function()
  {
	it('should have init and send set', function()
	{
	  expect(controller.init).toBeDefined();
	  expect(controller.send).toBeDefined();
	}); 
  });
  describe('Variables', function()
  {
	it('should init post', function()
	{
      expect(controller.post).toBeDefined();
	  //controller.init();
	  //expect(controller.post.user).toBeDefined();
	  //expect(controller.post.contact).toBeDefined();
	}); 
  });
});