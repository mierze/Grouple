'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var userMessages = {
  statement: 'SELECT send_date, message, sender, receiver FROM messages WHERE sender = ? '
      + 'AND receiver = ? OR sender = ? AND receiver = ? ORDER BY send_date ASC',
  emptyMessage: 'No messages to display.',
  params: ['email', 'contact', 'contact', 'email']
};

router.route('/:email/:contact').get(handler.wizard(userMessages));
module.exports = router;
