'use strict'
var handler = require('../../../handler');
var router = require('express').Router();

var joinGroup = {
  statement: '',
  successMessage: 'Successfully joined group!',
  params: ['email', 'id']
};

router.route('/').post(handler.wizard(joinGroup));
module.exports = router;
