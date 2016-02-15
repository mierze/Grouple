'use strict'
describe('Service: Getter', function() {
  beforeEach(module('grouple'));
  var getter, deferred, ULG, mock;
  beforeEach(inject(function(Getter, UserListGetter, $q) {
    deferred = $q.defer;
    getter = Getter;
    ULG = UserListGetter;
  }));
  
  //TODO: test each stateParams input and fetch to correct service
  describe('Getter', function() {
    it('Should return lists when passed url', inject(function($timeout) {
      //expect(getter.get).toBeDefined();
     // getter.get('https://groupleapp.herokuapp.com/api/user/list/friends/mierze@gmail.com'
       // ).passThrough();
      //console.log('how about this');
      expect(getter).toBeDefined();
      //expect($stateParams.content).toBe('groups');
      //expect(stateParams.id).toBe('mierze@gmail.com');
      //expect(controller.setTitle).toBeDefined();
      //expect(controller.fetchList).toBeDefined();
      //expect(valueToVerify).toEqual(5);
      //expect(controller.items).toBeDefined();
      //expect(controller.items).toBeGreaterThan(5);
    }));
    it('Should return lists when passed url', inject(function($timeout) {
      //expect(getter.get).toBeDefined();
    /*  spyOn(ULG.get('mierze@gmail.com', 'friends',
        function(data) {
          console.log('made it here!!');
         // console.log(JSON.stringify(data));
          expect(data).toBeDefined();
          console.log(data);
          
      }));*/
      //console.log('how about this');
      expect(ULG.get).toBeDefined();
      //expect($stateParams.content).toBe('groups');
      //expect(stateParams.id).toBe('mierze@gmail.com');
      //expect(controller.setTitle).toBeDefined();
      //expect(controller.fetchList).toBeDefined();
      //expect(valueToVerify).toEqual(5);
      //expect(controller.items).toBeDefined();
      //expect(controller.items).toBeGreaterThan(5);
    })); 
  });
});