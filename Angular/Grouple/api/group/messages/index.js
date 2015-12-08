var router = require('express').Router();

router.use('/', require('./friends'));
router.use('/remove', require('./remove'));
router.use('/send', require('./send'));

module.exports = router;