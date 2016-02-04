'use strict'
var handler = require('../handler');
var router = require('express').Router();

var accept = {
    statement: 'TODO: UPDATE friends SET rec_date = CURRENT_TIMESTAMP WHERE receiver = ? AND sender = ?',
    successMessage: 'Invite declined!',
    params: ['to', 'from']
};

router.route('/').put(handler.wizard(accept));
module.exports = router;