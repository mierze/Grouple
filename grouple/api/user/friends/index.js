var router = require('express').Router();

router.use('/', require('./friends'));
router.use('/invites', require('./invites'));

//router.use('/action', require('./action'));

module.exports = router;