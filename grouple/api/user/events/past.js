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
    //TODO: change db to match AS*
    mysql.query('SELECT events.e_id as id, events.e_name as name, events.min_part as minPart, events.max_part as maxPart, e_members.sender, e_members.rec_date, events.start_date '
      + 'FROM events JOIN e_members ON e_members.e_id = events.e_id WHERE e_members.rec_date is not null AND e_members.email = ? '
      + "AND events.eventstate = 'Ended' AND e_members.hidden is false ORDER BY events.start_date DESC",
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
        data.message = 'No past events to display.';
      }
      response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;