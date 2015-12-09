var mysql = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

//TODO: image uploading
router.route('/')
.post(function(request, response)
{
  var data = {};
  request.gender = 'm';
  if (!request.body.email || !request.body.first || !request.body.last || !request.body.birthday
    || !request.body.about || !request.body.location || !request.body.gender) 
  {
    data.success = -99;
    data.message = 'Missing email, first, last, birthday, about, location or gender.';
    response.json(data);
  }
  else
  {
    mysql.query('UPDATE users SET first = ?, last = ?, birthday = ?, about = ?, location = ?, gender = ? WHERE email = ?',
        [request.body.first, request.body.last, request.body.birthday,
        request.body.about, request.body.location, request.body.gender, request.body.email])
    .then(function(results)
    {
    console.log(JSON.stringify(results));
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