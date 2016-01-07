'use strict'
describe('Controller: GroupList', function() {
  beforeEach(module('grouple'));
  var $controller, controller, deferred;
  var stateParams = { content: 'groups', id: 'mierze@gmail.com' };
  beforeEach(inject(function(_$controller_, $q) {
    $controller = _$controller_;
    deferred = $q.defer;
    controller = $controller('GroupListController', {$stateParams: stateParams});
  }));

  //TODO: test each stateParams input and fetch to correct service
  describe('Variables', function() {
    it('fetch list should be defined and working', inject(function($timeout) {
      //expect($stateParams.content).toBe('groups');
      expect(stateParams.id).toBe('mierze@gmail.com');
      expect(controller.setTitle).toBeDefined();
      expect(controller.getGroups).toBeDefined();
    //expect(valueToVerify).toEqual(5);
     // expect(controller.items).toBeDefined();
      //expect(controller.items).toBeGreaterThan(5);
    })); 
  });
});