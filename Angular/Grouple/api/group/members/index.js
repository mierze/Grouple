var router = require('express').Router();

router.use('/members', require('./members'));
router.use('/non-members', require('./non-members'));

module.exports = router;