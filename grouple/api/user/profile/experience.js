var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());


//TODO Fetch each type of event statistic, calculate ..
router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('', request.body.id,
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