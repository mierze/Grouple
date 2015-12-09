var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('SELECT image_hdpi FROM events WHERE e_id = ?', request.body.id,
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
          //encode result
          data.image = results[0];  
        }
        else
        {
          data.success = -1;
          data.message = 'No profile image set.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;