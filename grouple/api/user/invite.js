'use strict'
var handler = require('../handler');
var router = require('express').Router();

var friendInvite = {
    statement: 'INSERT into friends (sender, receiver, send_date) VALUES (?, ?, CURRENT_TIMESTAMP)',
    successMessage: 'Friend invite sent!',
    params: ['from', 'to']
};

router.route('/').put(handler.wizard(friendInvite));
module.exports = router;