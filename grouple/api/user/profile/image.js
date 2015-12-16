'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var userImage = {
  statement: 'SELECT image_hdpi FROM users WHERE email = ?',
  emptyMessage: 'No image to display.',
  params: ['email']
};

router.route('/:email').get(handler.wizard(userImage));
module.exports = router;