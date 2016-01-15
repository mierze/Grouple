'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var deleteEventMessage = {
  statement: '',
  successMessage: 'Message deleted.',
  params: ['id']
};

router.route('/').get(handler.wizard(deleteEventMessage));
module.exports = router;
