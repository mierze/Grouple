'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventsPending = {
  statement: 'SELECT events.e_id as id, events.e_name as name, events.min_part, events.max_part, e_members.sender, e_members.rec_date, events.start_date '
              + 'FROM events JOIN e_members ON e_members.e_id = events.e_id where events.eventstate = "Proposed" AND '
              + 'e_members.rec_date is not null and e_members.email = ? and events.eventstate = "Proposed" ORDER BY events.start_date',
  emptyMessage: 'No pending events to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(eventsPending));
module.exports = router;