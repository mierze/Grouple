var mysql = require('../db');
var router = require('express').Router();
router.use(require('body-parser').json());
var bcrypt = require('bcrypt');

function registerWizard(request, response) {
  var data = {};
  if (!request.body.email || !request.body.password || !request.body.first) {
    data.success = -99;
    data.message = 'Missing email, password or first.';
    response.json(data);
  }
  else {
    mysql.query('SELECT COUNT(*) FROM users WHERE email = ?', request.body.email)
    .spread(function(results) {
      if (results[0].length)
      {
        data.message = 'Account already exists with that email.';
        data.success = -1;
        response.json(data);
      }
      else {
        if (!request.body.last)
          request.body.last = '';
        mysql.query('INSERT into users (email, password, first, last) VALUES (?, ?, ?, ?)',
          request.body.email, request.body.password, request.body.first, request.body.last)
        .spread(function(results) { 
          if (results.length) {
            data.success = 1;
            data.message = 'Registered successfully!';
          }
          else {
            data.success = -1;
            data.message = 'Error occured while registering.';
          }
        response.json(data);
        });
      }
    }) 
  }
};
router.route('/').post(registerWizard());

module.exports = router;