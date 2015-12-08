var router = require('express').Router();

router.use('/', require('./attending'));
router.use('/remove', require('./remove'));

module.exports = router;