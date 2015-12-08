var router = require('express').Router();

router.use('/join', require('./join'));
router.use('/leave', require('./leave'));
router.use('/delete', require('./delete'));

module.exports = router;