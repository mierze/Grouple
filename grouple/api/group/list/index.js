var router = require('express').Router();

router.use('/', require('./groups'));
router.use('/invites', require('./invites'));

//router.use('/action', require('./action'));

module.exports = router;