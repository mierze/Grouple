'use strict'
function UserImageGetter(Getter, ImageDecoder) {
  var vm = this;
  vm.get = get;

  return {
    get: vm.get
  };

  function get(email, cb) {
    vm.cb = cb;
    Getter.get('http://groupleapp.herokuapp.com/api/user/profile/image/' + email, callback);
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

module.exports = UserImageGetter;
