//db connect, TODO make this really hidden
var mysql = require('mysql');
module.exports.pool = mysql.createPool({
    host     : 'mysql2.gear.host',
    user     : 'mierze',
    password : 'Mb2VBG~II#A9',
    database : 'mierze'
});

