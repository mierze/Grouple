'use strict'
var handler = require('../../api/handler');

describe('Handler Functions', handlerFns);
  
//tests for functions server side
function handlerFns() {
  it('Set Params', function() {
      var _params = {id: 'mierze@gmail.com'};
      var _expected = ['id', 'id'];
      var params = handler.setParams(_params, _expected);
      expect(params.length).toBe(2);
      params.forEach(function(p) {
          expect(p).toBe('mierze@gmail.com');
      });
  });
  it('Set Params - None', function() {
      var _params = {id: 'mierze@gmail.com', test: 'extra'};
      var _expected = [];
      var params = handler.setParams(_params, _expected);
      expect(params.length).toBe(0);
  });
  it('Bundler', function() {
    //dummy data
    var data = {
      successMessage: 'SUCCESS'
    };
    var result = {
       
    };
  });
}
