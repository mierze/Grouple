'use strict'
module.exports = function($rootScope, $stateParams, $state, ProfileFetcher, ImageFetcher)
{ //profile controller
  //globals for user profile
  var vm = this,
  storage = window.localStorage,
  type = 'user',
  params = {};
  //TODO: need to check rank in group / event so that can show or hide editable
  vm.init = init;
  vm.toggleEdit = toggleEdit;
  
  //functions
  function init()
  { //start init function
    vm.post = {};
    vm.privs = {};
    vm.privs.admin = true;
    vm.showEdit = false;
    //case that id is for logged user's email
    if ($stateParams.id === 'user')   
      params.id = storage.getItem('email');  
    else if($stateParams.id !== null)   
      params.id = $stateParams.id;
    else
      alert('Invalid ID specified!');
    params.user = storage.getItem('email');
    fetchProfile();
  }; //end init function
  function toggleEdit()
  {
    if (vm.showEdit)
      vm.showEdit = false;
    else
      vm.showEdit = true;
  };
  function fetchProfile()
  {
    ProfileFetcher.fetch(params, type, function(data)
    { //start fetch profile
      if (data['success'] === 1)
      { //fetched successfully
        vm.info = data['info'];
        console.log('1');
        $rootScope.$broadcast('setTitle', (vm.info.first + ' ' + vm.info.last));
        //check for unset data
        console.log('1');
        if (vm.info.birthday == null)
          vm.birthdayNull = true;
        else
        { //parse age from birthday
        console.log('1');
          var birthday = new Date(vm.info.birthday); //to date
          var difference = new Date - birthday;
          vm.info.age = Math.floor((difference / 1000/*ms*/ / (60/*s*/ * 60/*m*/ * 24/*hr*/) ) / 365.25/*day*/);
        }
        console.log('1');
        if (vm.info.about == null)
          vm.aboutNull = true;
        if (vm.info.location == null)
          vm.locationNull = true;
        //end check for unset data
        console.log('1' + JSON.stringify(vm.info));
      }
      else //generic catch
        alert(data['message']);
    }); //end fetch profile
    /*
    ImageFetcher.fetch(params, type, function(data)
    { //start fetch image
      if (data['success'] === 1)
      {
        if (data['image'].length < 10  || data['image'] == null)
          vm.imageNull = true;
        else
        {
          var imgUrl = 'data:image/png;base64,' + data['image'];
          vm.image = imgUrl;
        }
      }
      else
      { //generic catch
        vm.imageNull = true;
        alert(data['message']);
      }
    }); //end fetch image
    */
  };
}; //end profile controller