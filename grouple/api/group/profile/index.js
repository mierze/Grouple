var router = require('express').Router();

router.use('/', require('./profile'));
//router.use('/', require('./action'));
//router.use('/experience', require('./experience'));

module.exports = router;