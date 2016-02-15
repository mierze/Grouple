var router = require('express').Router();

router.use('/', require('./profile'));
router.use('/image', require('./image'));
//router.use('/', require('./action'));
//router.use('/experience', require('./experience'));

module.exports = router;