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
    mysql.query('SELECT events.e_id as id, events.e_name as name, events.start_date FROM events '
      + 'JOIN e_members ON e_members.e_id = events.e_id WHERE '
      + "events.eventstate = 'Confirmed' AND e_members.email = ? AND "
      + 'events.start_date >= CURRENT_TIMESTAMP AND e_members.rec_date is not null '
      + 'order by events.start_date',
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
          data.message = 'No upcoming events to display.';
        }
        response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;