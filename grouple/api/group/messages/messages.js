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
    mysql.query('SELECT gm.message, gm.sender, gm.send_date, u.first, u.last FROM g_messages gm '
               + 'JOIN users u WHERE g_id = ? and gm.sender = u.email ORDER BY send_date ASC',
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