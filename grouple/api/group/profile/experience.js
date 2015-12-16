'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupExperience = {
  statement: '',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupExperience));
module.exports = router;