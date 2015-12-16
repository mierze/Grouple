'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var deleteGroupMessage = {
  statement: '',
  successMessage: 'Successfully joined event!',
  params: ['id', 'email']
};

router.route('/').delete(handler.wizard(deleteGroupMessage));
module.exports = router;
