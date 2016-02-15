'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventsDeclined = {
  statement: 'SELECT e.e_id as id, e.e_name as name, e.min_part, e.max_part, e.start_date '
              + 'FROM events e JOIN e_members em ON em.e_id = e.e_id WHERE creator = ? '
              + 'AND eventstate = "Declined" AND declined is false AND em.email = e.creator '
              + 'AND em.hidden is false ORDER BY start_date DESC',
  emptyMessage: 'No declined events to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(eventsDeclined));
module.exports = router;