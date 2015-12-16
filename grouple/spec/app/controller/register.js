'use strict'
describe('Controller: Register', function()
{
  beforeEach(module('grouple'));
  //first, last, email, password
  //test all combos
  var $controller, controller, Register, deferred;
  beforeEach(inject(function(_$controller_, _Register_, $q)
  {
    $controller = _$controller_;
    deferred = $q.defer;
    Register = _Register_;
    controller = $controller('RegisterController', {});
    spyOn(Register, 'register').and.returnValue(deferred.promise);
  }));
  
  describe('Asynchronous calls', function() {

  //expect register to not be null,
  
  //make a fake call to register and return each success
  
  //ensure controller handles all correctly
    it('should call asyncCall on myService', function() {
      //expect(Register.register).toHaveBeenCalled();
     // expect(Register.register.calls.count()).toBe(1);
    });

  });
  // Initialize the controller and a mock scope.
  describe('Functions', function()
  {
    it('should have register and showErrors functions set', function()
    {
      expect(controller.register).toBeDefined();
      expect(controller.showErrors).toBeDefined();
    }); 
  });
  describe('Variables', function()
  {
    it('should have post and post.last instantiated', function()
    {
      //expect(controller.storage).toBeDefined();
      expect(controller.post).toBeDefined();
      expect(controller.post.last).toEqual('');
    }); 
  });
});