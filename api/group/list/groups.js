'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var groups = {
  statement: 'SELECT g.g_name as name, g.g_id as id FROM groups g INNER JOIN g_members gm ON gm.g_id = g.g_id WHERE '
        + 'gm.rec_date IS not null and gm.email = ? ORDER BY g.g_name',
  emptyMessage: 'No groups to display.',
  params: ['id']
};

router.route('/:id').get(handler.wizard(groups));
module.exports = router;
