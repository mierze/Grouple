var router = require('express').Router();

router.use('/', require('./settings'));
//router.use('/password', require('./password'));

module.exports = router;