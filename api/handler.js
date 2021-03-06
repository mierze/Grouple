'use strict'
var mysql = require('./db');

//wizard does the db magic
function wizard(_data) {
  return function(request, response) {
    var params = setParams(request.params, _data.params);
    console.log(JSON.stringify(params));
    mysql.query(_data.statement, params)
    .spread(function(results) {
        var bundle = bundler(results, _data);
        response.json(bundle);
    }).catch(function(error) {
      console.log(error);
    });
  }
}

function postWizard(_data) {
    var data = setParams(_data.data, _data.params);
    console.log(JSON.stringify(data));
    mysql.query(_data.statement, data)
      .spread(function(results) {
          //return results;
          //var bundle = bundler(results, _data);
          var bundle = {};
          bundle.success = 1;
          bundle.message = _data.successMessage;
          return bundle.json();
      }).catch(function(error) {
        console.log(error);
      });
}

function putter(_data) {
    var data = setParams(_data.data, _data.params);
    console.log(JSON.stringify(data));
    mysql.query(_data.statement, data)
      .spread(function(results) {
          return JSON.stringify(results);
          //var bundle = bundler(results, _data);
          //return bundle.json();
      }).catch(function(error) {
        console.log(error);
      });
}

//sub functions
function setParams(_params, _expected) {
    var params = [];
    if (!_expected.length) { //set no params
      return params;
    }
    console.log(JSON.stringify(_params));

    _expected.forEach(function pushToParams(p) {
      params.push(_params[p]);
    });

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
module.exports.wizard = wizard;
module.exports.postWizard = postWizard;
