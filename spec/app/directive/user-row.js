describe("directive tests", function() {
    var element, scope, $compile;
 
    beforeEach(module('grouple')); // Name of the module my directive is in
    //beforeEach(module('module/list/user/part/user-row.html')); // The external template file referenced by templateUrl
 
    beforeEach(inject(function(_$compile_, $rootScope) {
        scope = $rootScope;
        $compile = _$compile_;
    }));
 
   /* it("compiles", function () {
        // Test whether an empty address is formatted correctly
        // Create an instance of the directive
        element = angular.element('<user-row></user-row>');
        $compile(element)(scope); // Compile the directive
        scope.$digest(); // Update the HTML
 
        // Get the isolate scope for the directive
        var isoScope = element.isolateScope();
 
        // Make our assertions
    });*/
});