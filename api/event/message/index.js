var router = require('express').Router();

router.use('/messages', require('./messages'));
//router.use('/send', require('./send'));
//router.use('/delete', require('./delete'));

module.exports = router;