'use strict'
var express = require('express');
var app = express();
var cors = require('cors');

//set port
app.set('port', (process.env.PORT || 1337));

//set views directory
app.use(express.static(__dirname + '/www'));

//def engine: app.engine('html', function(){});


app.use(cors());
function setHeader(req, res, next) {
  var allowedOrigins = ['localhost:1337', 'http://groupleapp.herokuapp.com', 'https://groupleapp.herokuapp.com'];
  //var origin = req.headers.origin;
  //if(allowedOrigins.indexOf(origin) > -1)
//    res.setHeader('Access-Control-Allow-Origin', origin);
 // res.setHeader('Access-Control-Allow-Origin', 'localhost:1337');
  //res.setHeader('Access-Control-Allow-Origin', 'http://groupleapp.herokuapp.com');
  res.setHeader("Access-Control-Allow-Origin", cors());
 // res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
  // Request headers you wish to allow
  //res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  next();
}

console.log('enabled cors');
//set api
app.use('/api', require('./api'));

//launch angular index page
app.get('/', function(request, response) {
  response.render('index.html');
});

//run server
app.listen(app.get('port'), function() {
  console.log('Grouple is now running on port...', app.get('port'));
});
