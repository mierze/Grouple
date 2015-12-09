var db = require('../../db');
var router = require('express').Router();
router.use(require('body-parser').json());

router.route('/:id')
.get(function(request, response)
{
  db.pool.getConnection(function(error, conn)
  {
    conn.query('SELECT emailFriendReq, emailGroupReq, emailEventReq, emailFriendMessage, emailGroupMessage, emailEventMessage, '
      + 'emailEventUpcoming, androidFriendReq, androidGroupReq, androidEventReq, androidFriendMessage, androidGroupMessage, androidEventMessage, androidEventUpcoming, androidUmbrella, emailUmbrella '
      + 'FROM users_settings WHERE email = ?',
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
          data.info = results;  
        }
        else
        {
          data.success = -1;
          data.message = 'No settings were found.';
        }
        response.json(data);
        conn.release();
    });
  });
});

module.exports = router;