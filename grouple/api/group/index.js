var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/members', require('./members'));
//router.use('/messages', require('./messages'));

module.exports = router;