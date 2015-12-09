var router = require('express').Router();

router.use('/accept', require('./friends'));
router.use('/decline', require('./decline'));
router.use('/leave', require('./leave'));

module.exports = router;