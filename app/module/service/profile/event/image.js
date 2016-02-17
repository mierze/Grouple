'use strict'
function EventImageGetter(Getter, ImageDecoder) {
  var vm = this;
  vm.get = get;

  return {
    get: vm.get
  };

  function get(id, cb) {
    vm.cb = cb;
    Getter.get('http://localhost:1337/api/event/profile/image/' + id, callback);
  }

  function callback(data) {
    //middleware for image callback
    //TODO set success based on stuff
    alert(JSON.stringify(data));
    var blob = data.data[0]['image_hdpi'];
    ImageDecoder.decode(blob, function setImage(image) {
      data.image = image;
      //call to main callback
      return vm.cb(data);
    });
  }
}

module.exports = EventImageGetter;
