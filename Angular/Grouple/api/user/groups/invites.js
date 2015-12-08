var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('SELECT u.email, u.first, u.last FROM users u '
               + 'INNER JOIN friends f ON f.sender = u.email WHERE f.receiver = ? '
               + 'AND rec_date IS NOT NULL UNION SELECT u.email, u.first, u.last '
               + 'FROM users u INNER JOIN friends f ON f.receiver = u.email WHERE f.sender = ? '
               + 'AND rec_date IS NOT NULL ORDER BY last', request.body.id, request.body.id,
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
          data.message = 'No friends to display.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;