var router = require('express').Router();

router.use('/', require('./messages'));
router.use('/remove', require('./remove'));
router.use('/send', require('./send'));

module.exports = router;