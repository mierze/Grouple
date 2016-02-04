var router = require('express').Router();

function setHeader(req, res, next) {
  var allowedOrigins = ['http://localhost:1337', 'http://groupleapp.herokuapp.com', 'https://groupleapp.herokuapp.com'];
  var origin = req.headers.origin;
  if(allowedOrigins.indexOf(origin) > -1)
    res.setHeader('Access-Control-Allow-Origin', origin);
  //res.setHeader('Access-Control-Allow-Origin', 'localhost:1337');
  //res.setHeader('Access-Control-Allow-Origin', 'http://groupleapp.herokuapp.com');
  //res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
  // Request headers you wish to allow
  //res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  next();
}

router.use(setHeader);
router.use('/user', require('./user'));
router.use('/group', require('./group'));
router.use('/event', require('./event'));
router.use('/session', require('./session'));

module.exports = router;