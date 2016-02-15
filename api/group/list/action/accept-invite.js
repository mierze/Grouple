'use strict'
var handler = require('../../../handler');
var router = require('express').Router();

//accept group invite
function acceptGroupInvite() {
  var data = {};
  data.statement = '';
  data.emptyMessage = '';
  data.successMessage = 'Acepted group invite!';
  data.params = ['id', 'email'];
  return data;
}

router.route('/').post(handler.wizard(acceptGroupInvite()));
module.exports = router;
