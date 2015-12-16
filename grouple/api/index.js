var router = require('express').Router();

function setHeader(req, res, next)
{
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
     // Request headers you wish to allow
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  next();
}
router.use(setHeader);
router.use('/user', require('./user'));
router.use('/group', require('./group'));
router.use('/event', require('./event'));
router.use('/session', require('./session'));

module.exports = router;