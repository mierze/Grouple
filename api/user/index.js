var router = require('express').Router();

router.use('/profile', require('./profile'));
router.use('/list', require('./list'));
router.use('/message', require('./message'));
<<<<<<< HEAD
router.use('/invite', require('./invite'));
=======
>>>>>>> b93fbae5c07aff0a6df31e79bf07359b2317e91a

module.exports = router;