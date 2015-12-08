var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/list', require('./list'));
router.use('/register', require('./members'));
router.use('/experience', require('./experience'));
router.use('/messages', require('./messages'));

module.exports = router;