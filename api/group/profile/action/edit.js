'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var editGroupProfile = {
  statement: 'UPDATE groups SET g_name = ?, about = ?, public = ? WHERE g_id = ?',
  successMessage: 'Successfully updated group profile!',
  params: ['name', 'about', 'pub', 'id']
};

router.route('/').put(handler.wizard(editGroupProfile));
module.exports = router;
