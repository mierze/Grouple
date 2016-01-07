'use strict'
var request = require('request');

describe('Profile Edit Tests', profileEditTests);

function profileEditTests() {
  var user = 'mierze@gmail.com';
  var gID = '223';
  var eID = '155';
  var base = 'http://localhost:1337/api';
  var reqURLs = {
    'User': base + '/user/profile/edit/' + user,
    'Group': base + '/group/profile/edit/' + gID,
    'Event': base + '/event/profile/edit/' + eID
  };
  checkRequests(reqURLs);
}

function checkRequests(_urls) {
  Object.keys(_urls).forEach(function(name) {                    
    it(name, function(done) {
      request(_urls[name],
      function(error, response, body) {
        if (!error) {
          expect(response.statusCode).toBe(200);
          var data = JSON.parse(body);
          if (data.success === 1)
            expect(data.data.length).toBeGreaterThan(0);
          else
            expect(data.message).toBeDefined();
          done();
        }
        else console.log(error);
      }, 250);
    });
  });
}