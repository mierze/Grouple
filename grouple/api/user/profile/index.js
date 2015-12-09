var router = require('express').Router();

//middleware to check for id?

router.use('/', require('./profile'));
//router.use('/image', require('./badges'));
//router.use('/badges', require('./badges'));
//router.use('/experience', require('./experience'));
router.use('/edit', require('./edit'));

module.exports = router;