var db = require('../../db');
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
      conn.query('SELECT joinable FROM events WHERE e_id = ?',
        request.body.email,
        function(error, results)
        {
          if (error)
          {
            data.success = -10;
            data.message = 'Error querying database.';
            console.log(error);
            response.json(data);
          }
          else if (results.length)
          {
            //ACCEPT HERE
            conn.query('SELECT joinable FROM events WHERE e_id = ?',
            request.body.email,
            function(error, results)
            {
              if (error)
              {
                data.success = -10;
                data.message = 'Error querying database.';
                console.log(error);
                response.json(data);
              }
              else if (results.length)
              {
                //ACCEPT HERE
                //A LOT NEEDS TO BE UPDATED, CONSIDER CALLING EXTERNAL SERVICE FILES
              }
              else
              {
                data.success = -2;
                data.message = 'Event is not currently joinable.';
                response.json(data);
              } 
            });
          }
          else
          {
            data.success = -2;
            data.message = 'Event is not currently joinable.';
            response.json(data);
          }
          conn.release();
        }
      );
    });
  }
});

module.exports = router;