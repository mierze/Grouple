var router = require('express').Router();
router.use('/login', require('./login'));
router.use('/register', require('./register'));
//router.use('/settings', require('./settings'));
//router.use('/update', require('./update'));
module.exports = router;
