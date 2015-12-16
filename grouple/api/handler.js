/*

coverage - working and tested:
    /session
[ ]     /register (post)
[ ]     /settings (get)(update)
    /user
        /list
[ ]         /friends (delete)
[ ]         /invites (update)(delete)
[ ]         /members (delete)
[ ]           /attending (get)(delete)
        /profile
[ ]         /edit (update)
[ ]         /delete (delete)
[ ]         /experience (get)
[ ]         /image (get)
[ ]     /invite (post)
[ ]     /messages (post)
[ ]         /contact (get)
    /group
        /list
[ ]       /groups (delete)
[ ]       /invites (delete)
        /profile
[ ]         /edit (update)
[ ]         /delete (delete)
[ ]         /experience (get)
[ ]         /image (get)
[ ]     /create (post)
[ ]     /messages (get)(post)
    //event
        /profile
[ ]           /edit (update)
[ ]           /delete (delete)
[ ]         /experience (get)
[ ]     /create (post)
[ ]       /messages (get)(post)   

*/
'use strict'
var mysql = require('./db');

function setParams(_params, _expected) {
    var params = [];
    if (!_expected.length)
    { //set no params
      return params;
    }
    
    _expected.forEach(function pushToParams(p)
    {
      params.push(_params[p]);
    });
    
    return params;
}

function bundler(results, _data) {
  var bundle = {};
  bundle.mod = 0;
  if (results.length) {
    bundle.success = 1;
    if (results.length > 1)
        bundle.data = results;
    else
        bundle.data = results[0];
    console.log(JSON.stringify(bundle.data));
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

function wizard(_data) {
  return function(request, response) {
    var params = setParams(request.params, _data.params);
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
  return function(request, response) {
    var post = setParams(request.post, _data.post);
    mysql.query(_data.statement, post)
    .spread(function(results) {
        var bundle = bundler(results, _data);
        response.json(bundle);
    }).catch(function(error) {
      console.log(error);
    });
  }
}
module.exports.setParams = setParams;
module.exports.wizard = wizard;
