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
    mysql.query('SELECT id, message, read_date, sender, receiver, send_date, u.first, u.last, u.image_mdpi '
      + 'FROM messages, users u WHERE u.email = receiver AND id in (SELECT MAX(id) AS id FROM messages WHERE sender = ? GROUP BY receiver) '
      + 'UNION SELECT id, message, read_date, sender, receiver, send_date, u.first, u.last, u.image_mdpi '
	  + 'FROM messages, users u WHERE u.email = sender AND id in (SELECT MAX(id) AS id FROM messages WHERE receiver = ? GROUP BY sender) '
      + 'ORDER BY id DESC',
      [request.params.id, request.params.id])
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
          data.message = 'No recent contacts to display.';
        }
        response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;