var router = require('express').Router();

router.use('/user', require('./user'));
router.use('/group', require('./group'));
router.use('/event', require('./event'));
router.use('/session', require('./session'));


module.exports = router;