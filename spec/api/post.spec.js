'use strict'
var request = require('request');

describe('Profile Edit Tests', profileEditSpecs);
describe('Register Test', registerSpec);
function profileEditSpecs() {
    var user = 'mierze@gmail.com';
    var gID = '223';
    var eID = '155';
    var base = 'http://localhost:1337/api';
    var userEditURL = base + '/user/profile/edit/' + user;
    var groupEditURL = base + '/group/profile/edit/' + gID;
    var eventEditURL = base + '/event/profile/edit/' + eID;
}
function registerSpec() {
    var email = 'testemail@email.com';
    var data = {
        'email': email,
        'first': 'bob',
        'last': '',
        'password': 'password'
    };
}