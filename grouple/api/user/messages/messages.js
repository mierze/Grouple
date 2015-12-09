var mysql = require('../../db');
var router = require('express').Router();

router.route('/:id?:contact')
.get(function(request, response)
{
  var data = {};
  if (!request.params.id || !request.params.contact)
  {
    data.success = -99;
    data.message = 'Missing id or contact.';
    response.json(data);
  }
  else
  {
    mysql.query('SELECT send_date, message, sender, receiver FROM messages WHERE sender = ? '
      + 'AND receiver = ? OR sender = ? AND receiver = ? ORDER BY send_date ASC',
      [request.params.contact, request.params.id, request.params.contact, request.params.id])
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