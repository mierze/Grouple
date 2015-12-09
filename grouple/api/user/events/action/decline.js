var db = require('../../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//TODO check post or something else for delete
router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.id || !request.body.email) 
  {
    data.success = -99;
    data.message = 'Missing id or email.';
    response.json(data);
  }
  else
  {
    db.pool.getConnection(function(error, conn)
    {
      conn.query('DELETE from e_members WHERE e_id = ? AND email = ?',	
        request.body.id, request.body.email,
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
              data.message = 'You have declined the event.';
          }
          else
          {
            data.success = -2;
            data.message = 'An error occured declining event, please try again.';
          }
          conn.release();
          response.json(data);
        }
      );
    });
  }
});

module.exports = router;