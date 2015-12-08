var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date "
               + "FROM events e JOIN e_members em ON em.e_id = e.e_id WHERE creator = ? "
               + "AND eventstate = 'Declined' AND declined is false AND em.email = e.creator "
               + "AND em.hidden is false ORDER BY start_date DESC",
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
          data.message = 'No declined events to display.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;