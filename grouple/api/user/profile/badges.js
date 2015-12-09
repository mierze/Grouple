var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('SELECT name, b_level, rec_date FROM badges WHERE email = ?',
      request.body.id,
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
          data.items = results;  
        }
        else
        {
          data.success = 0;
          data.message = 'No badges to display.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;