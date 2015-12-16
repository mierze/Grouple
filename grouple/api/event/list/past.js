'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventsPast = {
  statement: 'SELECT events.e_id as id, events.e_name as name, events.min_part as minPart, events.max_part as maxPart, e_members.sender, e_members.rec_date, events.start_date '
              + 'FROM events JOIN e_members ON e_members.e_id = events.e_id WHERE e_members.rec_date is not null AND e_members.email = ? '
              + 'AND events.eventstate = "Ended" AND e_members.hidden is false ORDER BY events.start_date DESC',
  emptyMessage: 'No past events to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(eventsPast));
module.exports = router;