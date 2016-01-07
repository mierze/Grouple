var router = require('express').Router();

router.use('/groups', require('./groups'));
router.use('/invites', require('./invites'));

//router.use('/action', require('./action'));

module.exports = router;