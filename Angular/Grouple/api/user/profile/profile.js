var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//USER PROFILE
router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('SELECT email as id, first, last, birthday, about, location, gender FROM users WHERE email = ?', request.body.id,
      function (error, results)
      {
        var data = {};
        data.mod = 0;
        if (error)
        {
          data.success = -10;
          data.message = 'Error querying database.';
          console.log(err);
        }
        else if (results.length)
        {
          data.success = 1;
          //if ...
            //data.mod = 1;
          data.info = results;  
        }
        else
        {
          data.success = -1;
          data.message = 'No profile info matching given ID.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;