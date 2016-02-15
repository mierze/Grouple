'use strict'
describe('Controller: Contact', function() {
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  beforeEach(inject(function(_$controller_, $q) {
    $controller = _$controller_;
    deferred = $q.defer;
    controller = $controller('ContactController', {});
  }));

  describe('Variables', function() {
    it('should have params set', function() {
      expect(controller.params).toBeDefined();
      expect(controller.params.email).toBeDefined();
    }); 
  });
});