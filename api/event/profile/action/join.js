'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var joinEvent = {
  statement: '',
  successMessage: 'Successfully joined event!',
  params: ['id', 'email']
};

router.route('/').post(handler.wizard(joinEvent));
module.exports = router;
