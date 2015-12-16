'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var contacts = {
  statement: 'SELECT id, message, read_date, sender, receiver, send_date, u.first, u.last, u.image_mdpi '
      + 'FROM messages, users u WHERE u.email = receiver AND id in (SELECT MAX(id) AS id FROM messages WHERE sender = ? GROUP BY receiver) '
      + 'UNION SELECT id, message, read_date, sender, receiver, send_date, u.first, u.last, u.image_mdpi '
	  + 'FROM messages, users u WHERE u.email = sender AND id in (SELECT MAX(id) AS id FROM messages WHERE receiver = ? GROUP BY sender) '
      + 'ORDER BY id DESC',
  emptyMessage: 'No recent contacts to display.',
  params: ['email', 'email']
};

router.route('/:email').get(handler.wizard(contacts));
module.exports = router;