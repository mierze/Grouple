'use strict'
function ImageGetter(Getter, ImageDecoder) {
  var vm = this;
  vm.get = get;
  
  return {
    get: vm.get
  };
  
  function get(id, cb) {
    vm.cb = cb;
    Getter.get('https://groupleapp.herokuapp.com/api/group/profile/image/' + id, callback);
  }
  
  function callback(data) {
    //middleware for image callback
    //TODO make sure success is accurate
    var blob = data.data[0]['image_hdpi'];
    ImageDecoder.decode(blob, function setImage(image) {
      //returns decoded image
      data.image = image;
      //call to main callback
      return vm.cb(data);
    });
  }
}

module.exports = ImageGetter;