'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var leaveEvent = {
  statement: '',
  successMessage: 'Successfully left event!',
  params: ['name', 'about', 'pub', 'id']
};

router.route('/').post(handler.wizard(leaveEvent));
module.exports = router;
