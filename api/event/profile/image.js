'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var eventImage = {
  statement: 'SELECT image_hdpi FROM events WHERE e_id = ?',
  emptyMessage: 'No image to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(eventImage));
module.exports = router;
