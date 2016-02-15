var router = require('express').Router();

router.use('/accept', require('./accept'));
router.use('/decline', require('./decline'));
<<<<<<< HEAD
=======
router.use('/invite', require('./invite'));
>>>>>>> b93fbae5c07aff0a6df31e79bf07359b2317e91a
router.use('/remove', require('./remove'));

module.exports = router;