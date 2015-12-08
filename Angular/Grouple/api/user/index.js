var router = require('express').Router();

router.use('/friends', require('./friends'));
//router.use('/groups', require('./groups'));
//router.use('/events', require('./events'));
//router.use('/messages', require('./messages'));
router.use('/profile', require('./profile'));

module.exports = router;