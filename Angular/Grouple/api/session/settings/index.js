var router = require('express').Router();

router.use('/password', require('./password'));
router.use('/update', require('./update'));

module.exports = router;