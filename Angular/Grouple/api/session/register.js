var db = require('../db');
var router = require('express').Router();
router.use(require('body-parser').json());
var bcrypt = require('bcrypt');

router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.email || !request.body.password || !request.body.first) 
  {
    data.success = -99;
    data.message = 'Missing email, password or first.';
    response.json(data);
  }
  else
  {
    db.pool.getConnection(function(error, conn)
    {
      conn.query('SELECT COUNT(*) FROM users WHERE email = ?', request.body.email,
      function(error, results)
      {
        if (error)
        {
          data.success = -10;
          data.message = 'Error querying database.';
          console.log(err);
          response.json(data);
        }
        else if (results[0])
        {
          data.message = 'Account already exists with that email.';
          data.success = -1;
          response.json(data);
        }
        else
        {
          conn.query('INSERT into users (email, password, first, last) VALUES (?, ?, ?, ?)', request.body.email, request.body.password, request.body.first, request.body.last,
          function(error, results)
          {
            if (error)
            {
              data.success = -11;
              data.message = 'Error querying database.';
              console.log(error);
            }
            else if (results.length)
            {
              data.success = 1;
              data.message = 'Registered successfully!';
            }
            else
            {
              data.success = -1;
              data.message = 'Error occured while registering.';
            }
            response.json(data);
          });
        }
        conn.release();
      });
    });
  }
});

module.exports = router;