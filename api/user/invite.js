'use strict'
var handler = require('../handler');
var router = require('express').Router();
router.use(require('body-parser').json());

var friendInvite = {
    statement: 'INSERT into friends (sender, receiver, send_date) VALUES (?, ?, CURRENT_TIMESTAMP)',
    successMessage: 'Friend invite sent!',
    params: ['from', 'to']
};

router.route('/').post(function(request, response) {
    friendInvite.data = request.body;
    console.log(JSON.stringify(friendInvite.data));
    var success = handler.postWizard(friendInvite);
    response.json("Successfully sent friend invitation!");
});


module.exports = router;
