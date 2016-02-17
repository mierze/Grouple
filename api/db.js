'use strict'
var mysql = require('mysql-promise')();
mysql.configure({
    host     : 'mysql2.gear.host',
    user     : 'mierze',
    password : 'Mb2VBG~II#A9',
    database : 'mierze'
});
module.exports = mysql;
