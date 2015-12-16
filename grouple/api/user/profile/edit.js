'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var editUserProfile = {
  statement: 'UPDATE users SET first = ?, last = ?, birthday = ?, about = ?, location = ?, gender = ? WHERE email = ?',
  successMessage: 'Successfully updated profile.',
  params: ['first', 'last', 'birthday', 'about', 'location', 'gender', 'email']
};

router.route('/').put(handler.wizard(editUserProfile));
module.exports = router;