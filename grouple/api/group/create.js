'use strict'
var handler = require('../handler');
//TODO: require get handler
var router = require('express').Router();

var createGroup = {
  statement: 'INSERT INTO groups (g_name, about, creator, public) VALUES (?,?,?,?)',
  successMessage: 'Successfully updated group profile!',
  post: ['name', 'about', 'creator', 'pub']
};

router.route('/').post(handler.wizard(createGroup));
module.exports = router;