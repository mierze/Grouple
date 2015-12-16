'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventInvites = {
  statement: 'SELECT e.e_id as id, e.e_name as name, e.min_part, e.max_part, e.start_date, em.sender '
              + 'FROM events e JOIN e_members em ON em.e_id = e.e_id WHERE em.rec_date '
              + 'is null AND em.email = ?',
  emptyMessage: 'No event invites to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(eventInvites));
module.exports = router;