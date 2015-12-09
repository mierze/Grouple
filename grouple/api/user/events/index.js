var router = require('express').Router();

router.use('/upcoming', require('./upcoming'));
router.use('/pending', require('./pending'));
router.use('/invites', require('./invites'));
router.use('/past', require('./past'));
router.use('/declined', require('./declined'));

//router.use('/action', require('./action'));

module.exports = router;