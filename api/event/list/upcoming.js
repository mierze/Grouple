'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventsUpcoming = {
  statement: 'SELECT events.e_id as id, events.e_name as name, events.start_date FROM events '
      + 'JOIN e_members ON e_members.e_id = events.e_id WHERE '
      + "events.eventstate = 'Confirmed' AND e_members.email = ? AND "
      + 'events.start_date >= CURRENT_TIMESTAMP AND e_members.rec_date is not null '
      + 'ORDER BY events.start_date',
  emptyMessage: 'No upcoming events to display.',
  params: ['email']
};

router.route('/:id').get(handler.wizard(eventsUpcoming));
module.exports = router;