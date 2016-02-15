'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupMembers = {
  statement: 'SELECT u.email, u.first, u.last FROM users u INNER JOIN g_members gm '
      + 'ON gm.email = u.email WHERE gm.g_id = ? AND gm.rec_date is not null ORDER BY u.last',
  emptyMessage: 'No group members to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupMembers));
module.exports = router;