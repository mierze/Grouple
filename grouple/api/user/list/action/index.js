var router = require('express').Router();

router.use('/accept', require('./accept'));
router.use('/decline', require('./decline'));
router.use('/remove', require('./remove'));

module.exports = router;