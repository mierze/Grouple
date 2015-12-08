var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query("SELECT events.e_id, events.e_name, events.min_part, events.max_part, e_members.sender, e_members.rec_date, events.start_date "
               + "FROM events JOIN e_members ON e_members.e_id = events.e_id where events.eventstate = 'Proposed' AND "
               + "e_members.rec_date is not null and e_members.email = ? and events.eventstate = 'Proposed' ORDER BY events.start_date",
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
          data.message = 'No pending events to display.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;