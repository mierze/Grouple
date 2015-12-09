var db = require('../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//TODO: image uploading
router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.id || !request.body.first || !request.body.last || !request.body.birthday
    || !request.body.about || !request.body.location || !request.body.gender) 
  {
    data.success = -99;
    data.message = 'Missing id, first, last, birthday, about, location or gender.';
    response.json(data);
  }
  else
  {
    db.pool.getConnection(function(error, conn)
    {
      conn.query('UPDATE users SET first = ?, last = ?, birthday = ?, about = ?, location = ?, gender = ? WHERE email = ?',
        request.body.first, request.body.last, request.body.birthday,
        request.body.about, request.body.location, request.body.gender, request.body.email,
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
          data.success = 1;
          data.message = 'Profile successfully updated!'; 
        }
        else
        {
          data.success = -2;
          data.message = 'Error updating profile, please try again.';
        }
        conn.release();
        response.json(data);
      });
    });
  }
});

module.exports = router;