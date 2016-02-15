'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groupInvites = {
  statement: 'SELECT g.g_id as id, g.g_name as name, gm.sender FROM groups g JOIN g_members gm '
        + 'ON gm.g_id = g.g_id WHERE gm.rec_date is null AND gm.email = ?',
  emptyMessage: 'No group invites to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groupInvites));
module.exports = router;