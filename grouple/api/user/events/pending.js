var mysql = require('../../db');
var router = require('express').Router();

router.route('/:id')
.get(function(request, response)
{
  var data = {};
  if (!request.params.id)
  {
    data.success = -99;
    data.message = 'Missing id.';
    response.json(data);
  }
  else
  {
    mysql.query('SELECT events.e_id as id, events.e_name as name, events.min_part, events.max_part, e_members.sender, e_members.rec_date, events.start_date '
               + "FROM events JOIN e_members ON e_members.e_id = events.e_id where events.eventstate = 'Proposed' AND "
               + "e_members.rec_date is not null and e_members.email = ? and events.eventstate = 'Proposed' ORDER BY events.start_date",
               request.params.id)
      .spread(function(results)
      {
        data.mod = 0;
        if (results.length)
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
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;