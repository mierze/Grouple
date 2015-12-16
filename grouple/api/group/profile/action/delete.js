'use strict'
var handler = require('../../handler');
var router = require('express').Router();

//delete group
function deleteGroup() {
  var data = {};
  data.statement = '';
  data.emptyMessage = '';
  data.successMessage = 'Successfully deleted group!';
  data.params = ['id'];
  return data;
}

router.route('/:id').delete(handler.wizard(deleteGroup()));

module.exports = router;