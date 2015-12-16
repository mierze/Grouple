'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var sendEventMessage = {
  statement: 'INSERT INTO e_messages (e_id, sender, message, send_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)',
  successMessage: 'Successfully sent message!',
  params: ['id', 'from', 'message']
};

router.route('/').post(handler.wizard(sendEventMessage));
module.exports = router;

