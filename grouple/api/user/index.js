var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/list', require('./list'));
router.use('/message', require('./message'));
router.use('/invite', require('./invite'));

module.exports = router;