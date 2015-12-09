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
    mysql.query('SELECT sender FROM friends WHERE receiver = ? AND rec_date is NULL',
      request.params.id)
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
        data.message = 'No friend invites to display.';
      }
      response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;