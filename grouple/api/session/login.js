var mysql = require('../db');

var router = require('express').Router();
router.use(require('body-parser').json());
var bcrypt = require('bcrypt');

router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.email || !request.body.password) 
  {
    data.success = -99;
    data.message = 'Missing email or password.';
    response.json(data);
  }
  else
  {
    mysql.query('SELECT email, password, first, last FROM users WHERE email = ?',
        request.body.email)
    .spread(function(results)
    {
      if (results.length)
      {
        if (bcrypt.compareSync(request.body.password, results[0].password))
        {
          data.success = 1;
          data.email = results[0].email;
          data.first = results[0].first;
          data.last = results[0].last;
          data.message = 'Success! Logging in...';
        }
        else
        {
          data.success = -1;
          data.message = 'Incorrect password.';
        }
      }
      else
      {
        data.success = -2;
        data.message = 'No user not found.';
       }
       response.json(data);
     }).error(function(error)
     {
         console.log(error);
     });
  }
});

module.exports = router;