var db = require('../../db');
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
    mysql.query('SELECT em.message, em.sender, em.send_date, u.first, u.last FROM e_messages em '
               + 'JOIN users u WHERE e_id = ? and em.sender = u.email ORDER BY send_date ASC',
      request.params.id)
      .spread(function(results)
      {
        data.mod = 0;
        if (results.length)
        {
          data.success = 1;
          data.messages = results;  
        }
        else
        {
          data.success = 0;
          data.message = 'No messages to display.';
        }
        response.json(data);
    }).catch(function(error)
    {
     console.log(error);
    });
  }
});

module.exports = router;