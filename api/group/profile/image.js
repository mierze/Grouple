'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupImage = {
  statement: 'SELECT image_hdpi FROM groups WHERE g_id = ?',
  emptyMessage: 'No image to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupImage));
module.exports = router;
