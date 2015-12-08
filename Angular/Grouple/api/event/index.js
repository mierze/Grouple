var router = require('express').Router();

router.use('/attending', require('./attending'));
router.use('/messages', require('./messages'));
router.use('/profile', require('./profile'));

module.exports = router;