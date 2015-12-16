'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventMessages = {
  statement: 'SELECT em.message, em.sender, em.send_date, u.first, u.last FROM e_messages em '
               + 'JOIN users u WHERE e_id = ? and em.sender = u.email ORDER BY send_date ASC',
  emptyMessage: 'No messages to display.',
  params: ['id']
};

router.route('/').get(handler.wizard(eventMessages));
module.exports = router;
