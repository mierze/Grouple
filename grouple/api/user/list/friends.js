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
      mysql.query('SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.sender = u.email WHERE f.receiver = ? AND rec_date IS NOT NULL UNION SELECT u.email, u.first, u.last FROM users u INNER JOIN friends f ON f.receiver = u.email WHERE f.sender = ? AND rec_date IS NOT NULL ORDER BY last',
      [request.params.id, request.params.id])
      .spread(function(results)
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
            data.message = 'No friends to display.';
          }
          response.json(data);
      }).catch(function(error)
      {
        console.log(error);
      });
    }
});

module.exports = router;