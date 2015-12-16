'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var userExperience = {
  statement: '',
  emptyMessage: '',
  params: ['email']
};

router.route('/:email').get(handler.wizard(userExperience));
module.exports = router;