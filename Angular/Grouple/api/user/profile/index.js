var router = require('express').Router();

//middleware for user profile
router.use(function(request, response, next)
{
  next();
});

router.use('/', require('./profile'));
//router.use('/badges', require('./badges'));

module.exports = router;