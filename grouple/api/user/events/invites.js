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
    mysql.query('SELECT e.e_id as id, e.e_name as name, e.min_part, e.max_part, e.start_date, em.sender '
               + 'FROM events e JOIN e_members em ON em.e_id = e.e_id WHERE em.rec_date '
               + 'is null and em.email = ?',
      request.params.id)
      .then(function(results)
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
          data.message = 'No event invites to display.';
        }
        response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;