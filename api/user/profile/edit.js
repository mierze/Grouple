'use strict'
var handler = require('../../handler');
var router = require('express').Router();
router.use(require('body-parser').json());

var editUserProfile = {
  statement: 'UPDATE users SET first = ?, last = ?, birthday = ?, about = ?, location = ?, gender = ? WHERE email = ?',
  successMessage: 'Successfully updated profile.',
  params: ['first', 'last', 'birthday', 'about', 'location', 'gender', 'email']
};

router.route('/').post(function(request, response) {
    console.log(JSON.stringify(request.body));
    editUserProfile.data = request.body;
    console.log(JSON.stringify(editUserProfile.data));
    response.json(handler.postWizard(editUserProfile));
});

module.exports = router;
