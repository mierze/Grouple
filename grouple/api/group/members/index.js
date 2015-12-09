var router = require('express').Router();

router.use('/', require('./members'));
//router.use('/non', require('./non-members'));

module.exports = router;