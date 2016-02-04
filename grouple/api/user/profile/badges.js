'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var badges = {
  statement: 'SELECT name, level, rec_date FROM badges WHERE email = ?',
  emptyMessage: 'No badges to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(badges));
module.exports = router;