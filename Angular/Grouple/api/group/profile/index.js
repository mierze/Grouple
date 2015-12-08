var router = require('express').Router();

router.use('/', require('./profile'));
router.use('/edit', require('./edit'));
router.use('/join', require('./join'));
router.use('/leave', require('./leave'));
router.use('/experience', require('./experience'));

module.exports = router;