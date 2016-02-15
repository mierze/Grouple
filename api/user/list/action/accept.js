'use strict'
var handler = require('../handler');
var router = require('express').Router();

var accept = {
    statement: 'UPDATE friends SET rec_date = CURRENT_TIMESTAMP WHERE receiver = ? AND sender = ?',
    successMessage: 'Invite accepted!',
    params: ['to', 'from']
};

router.route('/').put(handler.wizard(accept));
module.exports = router;