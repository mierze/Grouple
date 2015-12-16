'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupProfile = {
  statement: 'SELECT g_name as name, about, creator, public, date_created FROM groups WHERE g_id = ?',
  emptyMessage: 'No profile info to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupProfile));
module.exports = router;