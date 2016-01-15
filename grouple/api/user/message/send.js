'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var sendMessage = {
  statement: 'INSERT INTO messages (receiver, sender, message, send_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)',
  successMessage: 'asdf',
  params: ['to', 'from', 'message']
};

router.route('/').post(handler.wizard(sendMessage));
module.exports = router;