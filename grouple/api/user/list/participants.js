'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventParticipants = {
  statement: 'SELECT u.email, u.first, u.last FROM users u JOIN e_members em ON em.email = u.email WHERE em.e_id = ? AND em.rec_date IS not null ORDER BY u.last',
  emptyMessage: 'No participants for this event.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(eventParticipants));
module.exports = router;