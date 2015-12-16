'use strict'
var express = require('express');

//setup express
var app = express();
var api = require('./api');


app.set('port', (process.env.PORT || 3000));
app.use(express.static(__dirname + '/www'));

// views is directory for all template files
app.set('views', __dirname + '/www');
app.engine('html', function(){});

app.get('/', function(request, response) {
  response.render('index.html');
});

app.use('/api', api);

app.listen(app.get('port'), function() {
  console.log('Grouple is now running on port...', app.get('port'));
});


