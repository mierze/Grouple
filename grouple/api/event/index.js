var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/messages', require('./messages'));
router.use('/list', require('./list'));

//router.use('/create', require('./create'));

module.exports = router;