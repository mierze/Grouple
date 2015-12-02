'use strict'
describe('Controller: Event Profile', function()
{
  beforeEach(module('grouple'));
  var $controller, controller, deferred, ProfileFetcher;
  beforeEach(inject(function(_$controller_, _ProfileFetcher_, $q)
  {
    $controller = _$controller_;
    ProfileFetcher = _ProfileFetcher_;
    deferred = $q.defer;
    controller = $controller('EventProfileController', {});
  }));

  describe('Functions', function()
  {
    it('should have init, toggleEdit set', function()
    {
      var post = {};
      post.id = '223';
      post.user =  'mierze@gmail.com';
      var fun = function(data)
      {
        console.log("made it here");
        alert(JSON.stringify(data));
        expect(data.success).toBeDefined();
      };
      //expect(ProfileFetcher.fetch()).toBeDefined()
      spyOn(ProfileFetcher, 'fetch').and.returnValue(deferred.promise);
      ProfileFetcher.fetch(post, 'event', fun);
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