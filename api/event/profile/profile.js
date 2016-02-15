'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventProfile = {
  statement: 'SELECT e_id as id, e_name as name, eventstate, start_date, '
    + 'end_date, recurring_type, category, about, location, min_part, max_part, '
    + 'creator, public FROM events WHERE e_id = ?',
  emptyMessage: 'No profile info to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(eventProfile));
module.exports = router;

