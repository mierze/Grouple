var router = require('express').Router();

router.use('/friends', require('./friends'));
router.use('/invites', require('./invites'));
router.use('/members', require('./members'));
//router.use('/non-members', require('./non-members'));
router.use('/attending', require('./attending'));

//router.use('/action', require('./action'));

module.exports = router;