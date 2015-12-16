'use strict'
var handler = require('../../handler');
var router = require('express').Router();

//join event
function joinEvent() {
  var data = {};
  data.statement = '';
  data.emptyMessage = '';
  data.successMessage = 'Successfully joined event!';
  data.params = ['id', 'email'];
  return data;
}

router.route('/').post(handler.wizard(joinEvent()));
module.exports = router;
