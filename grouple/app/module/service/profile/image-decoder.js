'use strict'
function ImageDecoder() {
  var vm = this;
  vm.decode = decode;
  
  return {
    decode: vm.decode
  };
  
  function decode(blob, callback) {
    var buffer = new Buffer(blob, 'binary');
    return callback(buffer.toString('base64'));
  }
}

module.exports = ImageDecoder;