var router = require('express').Router();

router.use('/groups', require('./groups'));
router.use('/invites', require('./register'));
router.use('/settings', require('./settings'));

module.exports = router;