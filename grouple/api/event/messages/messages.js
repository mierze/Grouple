var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//TODO pass user id and check mods?
router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('SELECT em.message, em.sender, em.send_date, u.first, u.last FROM e_messages em '
               + 'JOIN users u WHERE e_id = ? and em.sender = u.email ORDER BY send_date ASC',
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
          data.messages = results;  
        }
        else
        {
          data.success = 0;
          data.message = 'No messages to display.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;