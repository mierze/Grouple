'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var friendInvites = {
  statement: 'SELECT sender FROM friends WHERE receiver = ? AND rec_date is NULL',
  emptyMessage: 'No friend invites to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(friendInvites));
module.exports = router;