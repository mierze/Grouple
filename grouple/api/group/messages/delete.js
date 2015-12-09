var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.id) 
  {
    data.success = -99;
    data.message = 'Missing id.';
    response.json(data);
  }
  else
  {
    db.pool.getConnection(function(error, conn)
    {
      conn.query('',
        request.body.id,
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
              data.message = 'Message has been deleted!';
          }
          else
          {
            data.success = -2;
            data.message = 'Error deleting message.';
          }
          conn.release();
          response.json(data);
        }
      );
    });
  }
});

module.exports = router;