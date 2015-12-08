var router = require('express').Router();

router.use('/', require('./profile'));
router.use('/action', require('./action'));

module.exports = router;