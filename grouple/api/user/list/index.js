var router = require('express').Router();

router.use('/friends', require('./friends'));
router.use('/friend-invites', require('./friend-invites'));
router.use('/group-members', require('./group-members'));
//router.use('/non-members', require('./non-members'));
router.use('/event-participants', require('./event-participants'));

//router.use('/action', require('./action'));

module.exports = router;