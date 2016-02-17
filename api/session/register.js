var mysql = require('../db');
var router = require('express').Router();
router.use(require('body-parser').json());
var bcrypt = require('bcrypt');

function registerWizard() {

    return function (request, response) {
        var data = {};

        if (!request.body.email || !request.body.password || !request.body.first) {
            data.success = -99;
            data.message = 'Missing email, password or first.';
            response.json(data);
        }
        else {
            console.log(request.body.email);
            mysql.query('SELECT COUNT(email) as count FROM users WHERE email = ?', request.body.email)
                .spread(function (results) {
                    if (results[0].count) {
                        data.message = 'Account already exists with that email.';
                        data.success = -1;
                        response.json(data);
                    }
                    else {
                        if (!request.body.last)
                            request.body.last = '';
                        mysql.query('INSERT into users (email, password, first, last) VALUES (?, ?, ?, ?)',
                            getPackage(request.body.email, request.body.password, request.body.first, request.body.last))
                            .spread(function (results) {
                                if (results.affectedRows) {
                                    data.success = 1;
                                    data.message = 'Registered successfully!';
                                }
                                else {
                                    data.success = -1;
                                    data.message = 'Error occurred while registering.';
                                }
                                response.json(data);
                            });
                    }
                }).catch(function (error) {
                console.log(error);
            });
        }
    }
}
function getPackage(email, password, first, last) {
    var package = [email, crypt(password), first, last];
    return package;
}
function crypt(password) {
    var salt = bcrypt.genSaltSync(10);
    return bcrypt.hashSync(password, salt);
}
router.route('/').post(registerWizard());

module.exports = router;
