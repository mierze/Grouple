'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var sendGroupMessage = {
  statement: 'INSERT INTO g_messages (g_id, sender, message, send_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)',
  successMessage: 'Successfully sent group message!',
  params: ['id', 'from', 'message']
};

router.route('/').post(handler.wizard(sendGroupMessage));
module.exports = router;
