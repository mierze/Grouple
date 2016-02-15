'use strict'
var express = require('express');
var app = express();

//set port
app.set('port', (process.env.PORT || 1337));

//set views directory
app.use(express.static(__dirname + '/www'));

//def engine: app.engine('html', function(){});

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