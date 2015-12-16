'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var leaveGroup = {
  statement: '',
  successMessage: 'Successfully left group!',
  params: ['id', 'email']
};

router.route('/:id/:email').delete(handler.wizard(leaveGroup));
module.exports = router;