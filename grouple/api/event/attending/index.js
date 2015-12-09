var router = require('express').Router();

router.use('/', require('./attending'));
//router.use('/delete', require('./delete'));

module.exports = router;