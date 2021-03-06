'use strict'
describe('Controller: Login', function () {
    beforeEach(module('grouple'));
    //first, last, email, password
    //test all combos
    var $controller, controller, deferred;
    beforeEach(inject(function (_$controller_, $q) {
        $controller = _$controller_;
        deferred = $q.defer;
        controller = $controller('LoginController', {});
    }));

    describe('Functions', function () {
        it('should have register and showErrors functions set', function () {
            expect(controller.login).toBeDefined();
            expect(controller.showErrors).toBeDefined();
            expect(controller.enter).toBeDefined();
        });
    });
    describe('Variables', function () {
        it('should have post and post.last instantiated', function () {
            //expect(controller.storage).toBeDefined();
            expect(controller.post).toBeDefined();
            //expect(controller.post.last).toEqual('');
        });
    });
    describe('Successful Login', function () {
       it('should login with this post information', function () {
            controller.post.email = 'mierze@gmail.com';
           controller.post.password = 'pass';
           controller.login();
        });
    });
});