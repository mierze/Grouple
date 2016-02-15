'use strict'
var mysql = require('./db');

//wizard does the db magic
function wizard(_data) {
  return function(request, response) {
    var params = setParams(request.params, _data.params);
<<<<<<< HEAD
    console.log(JSON.stringify(_data) + '\n\n' + params);
=======
>>>>>>> b93fbae5c07aff0a6df31e79bf07359b2317e91a
    mysql.query(_data.statement, params)
    .spread(function(results) {
        var bundle = bundler(results, _data);
        response.json(bundle);
    }).catch(function(error) {
      console.log(error);
    });
  }
}

//sub functions
function setParams(_params, _expected) {
    var params = [];
    if (!_expected.length) { //set no params
      return params;
    }
<<<<<<< HEAD

    _expected.forEach(function pushToParams(p) {
      params.push(_params[p]);
    });

=======
    
    _expected.forEach(function pushToParams(p) {
      params.push(_params[p]);
    });
    
>>>>>>> b93fbae5c07aff0a6df31e79bf07359b2317e91a
    return params;
}

function bundler(results, _data) {
  var bundle = {};
  bundle.mod = 0;
  if (results.length) {
    bundle.success = 1;
    //TODO: for 1 row stuff this breaks
    //check successes down the pipe
    if (results.length)
        bundle.data = results;
    else
        bundle.data = results;
   // console.log(JSON.stringify(results));
    if (_data.successMessage)
        bundle.message = _data.successMessage;
  }
  else {
    bundle.success = 0;
    if (_data.emptyMessage)
        bundle.message = _data.emptyMessage;
  }
  return bundle;
}

module.exports.setParams = setParams;
<<<<<<< HEAD
module.exports.wizard = wizard;
=======
module.exports.wizard = wizard;
>>>>>>> b93fbae5c07aff0a6df31e79bf07359b2317e91a
