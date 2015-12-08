var db = require('../db');
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
    db.pool.getConnection(function(error, conn)
    {
      conn.query('SELECT email, password, first, last FROM users WHERE email = ?', request.body.email,
      function(error, results)
      {
        if (error)
        {
          data.success = -10;
          data.message = 'Error querying database.';
          console.log(error);
        }
        else if (results.length)
        {
          if (bcrypt.compareSync(request.body.password, results[0].password))
          {
            data.success = 1;
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
        conn.release();
        response.json(data);
      });
    });
  }
});

module.exports = router;