'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var friends = {
  statement: 'SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.sender = u.email WHERE f.receiver = ? AND rec_date IS NOT NULL UNION SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.receiver = u.email WHERE f.sender = ? AND rec_date IS NOT NULL ORDER BY last',
  emptyMessage: 'No friends to display.',
  params: ['email', 'email']
};

router.route('/:email').get(handler.wizard(friends));
module.exports = router;