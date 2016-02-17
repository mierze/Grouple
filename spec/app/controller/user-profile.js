'use strict'
describe('Controller" User Profile', function() {
  beforeEach(module('grouple'));
  //first, last, email, password
  //test all combos
  var $controller, controller, deferred;
  beforeEach(inject(function(_$controller_, $q) {
	$controller = _$controller_;
	deferred = $q.defer;
	//Register = _Register_;
	//scope= {};
	controller = $controller('UserProfileController', {});
  }));

  // Initialize the controller and a mock scope.
  describe('Functions', function() {
	it('should have init and toggleEdit functions set', function() {
	  expect(controller.init).toBeDefined();
	  expect(controller.toggleEdit).toBeDefined();
	}); 
  });
  describe('Variables', function() {
	it('should have post and post.last instantiated', function() {
	  //run init first
	 // controller.init();
      //expect(controller.post).toBeDefined();
     // expect(controller.privs).toBeDefined();
	  //expect(controller.privs.admin).toBeDefined();
	}); 
  });
});