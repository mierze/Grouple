var router = require('express').Router();

router.use('/', require('./profile'));
router.use('/image', require('./image'));
router.use('/badges', require('./badges'));
router.use('/experience', require('./experience'));
router.use('/edit', require('./edit'));

module.exports = router;