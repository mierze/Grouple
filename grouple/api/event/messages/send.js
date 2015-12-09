var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.id || !request.body.from || !request.body.message) 
  {
    data.success = -99;
    data.message = 'Missing id, from, or message.';
    response.json(data);
  }
  else
  {
    db.pool.getConnection(function(error, conn)
    {
      conn.query('INSERT INTO e_messages (e_id, sender, message, send_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)',
        request.body.id, request.body.from, request.body.message,
        function(error, results)
        {
          if (error)
          {
            data.success = -10;
            data.message = 'Error querying database.';
            console.log(error);
          }
          else if (results.length)
          {
              data.success = 1;
              data.message = 'Message sent successfully!';
          }
          else
          {
            data.success = -2;
            data.message = 'Message failed to send.';
          }
          conn.release();
          response.json(data);
        }
      );
    });
  }
});

module.exports = router;