'use strict'
var request = require('request');

describe('List Tests', listTests);
describe('Profile Tests', profileTests);
describe('Image Tests', imageTests);

function listTests() {
  var user = 'mierze@gmail.com';
  var base = 'http://localhost:1337/api';
  var reqURLs = {
    'Friends': base + '/user/list/friends/' + user,
    'Friend Invites': base + '/user/list/friend-invites/' + user,
    'Groups': base + '/group/list/groups/' + user,
    'Group Invites': base + '/group/list/invites/' + user,
    'Upcoming Events': base + '/event/list/upcoming/' + user,
    'Pending Events': base + '/event/list/pending/' + user,
    'Event Invites': base + '/event/list/invites/' + user,
    'Past Events': base + '/event/list/past/' + user,
    'Declined Events': base + '/event/list/declined/' + user
  };
  checkRequests(reqURLs);
}

function profileTests() {
  var email = 'mierze@gmail.com';
  var gID = '223';
  var eID = '103';
  var base = 'http://localhost:1337/api';
  var reqURLs = {
    'User': base + '/user/profile/' + email,
    'Group': base + '/group/profile/' + gID,
    'Event': base + '/event/profile/' + eID
  };
  checkRequests(reqURLs);
}

function imageTests() {
  var email = 'mierze@gmail.com';
  var gID = '223';
  var eID = '103';
  var base = 'http://localhost:1337/api';
  var reqURLs = {
    'User': base + '/user/profile/image/' + email,
    'Group': base + '/group/profile/image/' + gID,
    'Event': base + '/event/profile/image/' + eID
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