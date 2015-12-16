'use strict'
var handler = require('../../api/handler');
var request = require('request');
  
describe('Live API', liveAPI);

//tests for requests on live api
function liveAPI() {
  it('Friends Call', function(done) {
    request('https://groupleapp.herokuapp.com/api/user/list/friends/mierze@gmail.com',
    function(error, response, body) {
      if (!error) {
        console.log(response.statusCode);
        expect(response.statusCode).toBe(200);
        //var data = JSON.parse(body);
        //expect(data.data.length).toBeGreaterThan(0);
        //expect(data.success).toBe(1);
        done();
      }
      else console.log(error);
    }, 250);
    
  });
  
  it('Groups Call', function(done) {
    request('localhost:3000/api/group/list/mierze@gmail.com',
    function(error, response, body) {
      if (!error) {
        console.log(response.statusCode);
        expect(response.statusCode).toBe(200);
        var data = JSON.parse(body);
        expect(data.data.length).toBeGreaterThan(0);
        expect(data.success).toBe(1);
        done();
      }
      else console.log(error);
    }, 250);
    
  });
}