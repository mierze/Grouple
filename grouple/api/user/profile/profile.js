'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var userProfile = {
  statement: 'SELECT email, first, last, birthday, about, location, gender FROM users WHERE email = ?',
  emptyMessage: 'No profile info to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(userProfile));
module.exports = router;