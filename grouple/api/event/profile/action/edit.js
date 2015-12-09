var mysql = require('../../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//TODO: image uploading
router.route('/')
.post(function(request, response)
{
	
  if(!request.body.id || !request.body.name || !request.body.pub || !request.body.about
	   || !request.body.startDate || !request.body.endDate
	   || !request.body.category || !request.body.minPart
	   || !request.body.maxPart || !request.body.recType
	   || !request.body.location)
  {
    data.success = -99;
    data.message = 'Missing id, name, pub, about, startDate, endDate, maxPart, recType or location';
    response.json(data);
  }
  else
  {
    mysql.query('UPDATE events SET e_name = ?, public = ?, about = ?, start_date = ?, end_date = ?, category = ?, min_part = ?, max_part = ?, recurring_type = ?, location = ? WHERE e_id = ?',
		[request.body.name, request.body.pub, request.body.about, request.body.endDate, request.body.category,
         request.body.minPart, request.body.maxPart, request.body.recType, request.body.location, request.body.id])
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