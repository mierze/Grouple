'use strict'
describe('Controller: Group Profile', function() {
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  beforeEach(inject(function(_$controller_, $q) {
    $controller = _$controller_;
    deferred = $q.defer;
    controller = $controller('GroupProfileController', {});
  }));

  describe('Functions', function() {
    it('should have init and toggleEdit set', function() {
      expect(controller.init).toBeDefined();
      expect(controller.toggleEdit).toBeDefined();
    }); 
  });
  describe('Variables', function() {
    it('should have post and post.last instantiated', function() {
      //expect(controller.storage).toBeDefined();
     // expect(controller.post).toBeDefined();
     // expect(controller.post.last).toEqual('');
    }); 
  });
});