var router = require('express').Router();

router.use('/', require('./messages'));
router.use('/contact', require('./contact'));
//router.use('/send', require('./send'));
//router.use('/delete', require('./delete'));

module.exports = router;