'use strict'
var handler = require('../../handler');
var router = require('express').Router();

var editEventProfile = {
  statement: 'UPDATE events SET e_name = ?, public = ?, about = ?, start_date = ?, '
    + 'end_date = ?, category = ?, min_part = ?, max_part = ?, recurring_type = ?, location = ? WHERE e_id = ?',
  successMessage: 'Successfully updated event profile!',
  params: ['name', 'pub', 'about', 'startDate', 'endDate', 'category', 'minPart', 'maxPart', 'recType', 'location', 'id']
};

router.route('/').put(handler.wizard(editEventProfile));
module.exports = router;
