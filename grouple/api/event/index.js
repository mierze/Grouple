var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/attending', require('./attending'));
//router.use('/messages', require('./messages'));

module.exports = router;