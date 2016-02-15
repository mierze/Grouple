'use strict'
describe('Controller: Entity Message', function() {
  beforeEach(module('grouple'));
  var $controller, controller, deferred, stateParams = {'content': 'group', 'id': '200'};
  beforeEach(inject(function(_$controller_, $q) {
	$controller = _$controller_;
	deferred = $q.defer;
	controller = $controller('EntityMessageController', {$stateParams: stateParams});
  }));

  describe('Functions', function() {
	it('should have init and send set', function() {
	  expect(controller.init).toBeDefined();
	  expect(controller.send).toBeDefined();
	}); 
  });
  describe('Variables', function() {
	it('should init post', function() {
      expect(controller.post).toBeDefined();
	  controller.init();
      expect(controller.post.id).toBeDefined();
      expect(controller.post.user).toBeDefined();
	  expect(controller.post.from).toBeDefined();
	}); 
  });
  
 describe('when events', function() {
	beforeEach(function() {
  
	  // you have called it 'something' in your controller not 'isSomething'
	  stateParams.content = 'events';
	  // instantiate a new controller with the updated $stateParams object
	  ctrl = $controller('EntityMessageController', {
		 $stateParams: stateParams
	   });
	});
  });
});