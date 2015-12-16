'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupNonMembers = {
  statement: 'SELECT u.email, u.first, u.last FROM users u INNER JOIN g_members gm '
      + 'ON gm.email = u.email WHERE gm.g_id = ? AND gm.rec_date is not null ORDER BY u.last',
  emptyMessage: 'All of your friends are already in this group.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupNonMembers));
module.exports = router;