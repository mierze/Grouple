'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupMessages = {
  statement: 'SELECT gm.message, gm.sender, gm.send_date, u.first, u.last FROM g_messages gm '
               + 'JOIN users u WHERE g_id = ? and gm.sender = u.email ORDER BY send_date ASC',
  emptyMessage: 'No messages to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupMessages));
module.exports = router;