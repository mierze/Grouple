'use strict';
module.exports = angular.module('service.profile', [
  require('./user').name,
  require('./group').name,
  require('./event').name
])
.factory('ImageDecoder', require('./image-decoder'));
