var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/message', require('./message'));
router.use('/list', require('./list'));

//router.use('/create', require('./create'));

module.exports = router;