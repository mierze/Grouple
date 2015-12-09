var mysql = require('../../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//TODO: image uploading
router.route('/')
.post(function(request, response)
{
  var data = {};
  if (!request.body.id || !request.body.name || !request.body.pub || !request.body.about)
  {
    data.success = -99;
    data.message = 'Missing id, name, pub or about.'
    response.json(data);
  }
  else
  {
    mysql.query('UPDATE groups SET g_name = ?, about = ?, public = ? WHERE g_id = ?',
	  [request.body.name, request.body.about, request.body.pub, request.body.id])
    .spread(function(results)
    {
     if (results.length)
      {
        data.success = 1;
        data.message = 'Profile successfully updated!'; 
      }
      else
      {
        data.success = -2;
        data.message = 'Error updating profile, please try again.';
      }
      response.json(data);
    }).catch(function(error)
    {
      console.log(error);
    });
  }
});

module.exports = router;