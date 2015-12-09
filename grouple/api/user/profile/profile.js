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
    mysql.query('SELECT email, first, last, birthday, about, location, gender FROM users WHERE email = ?',
      request.params.id)
      .spread(function(results)
      {
        data.mod = 0;
        if (results.length)
        {
          data.success = 1;
          //if ...
            //data.mod = 1;
          data.info = results[0];  
        }
        else
        {
          data.success = -1;
          data.message = 'No profile info found.';
        }
        response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;