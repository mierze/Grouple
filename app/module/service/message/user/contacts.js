'use strict'
function Contacts(Getter) {
    var vm = this;
    vm.get = get;
    
    return {
        get: vm.get
    };
    
    function get(email, cb) {
        vm.cb = cb;
        vm.email = email;
        Getter.get('https://groupleapp.herokuapp.com/api/user/message/contacts/' + email, callback);
    }
    
    function callback(data) {
        //contact callback middleware
        
        alert(JSON.stringify(data.data));
        var parsed = {};
        (data.data).forEach(function (message) {
            var contact = message.receiver === vm.email ? message.sender : message.receiver;
            message.contact = contact;
            if (parsed[contact] == null)
                parsed[contact] = message;
        });
        data.data = parsed;
        alert(JSON.stringify(data));
        return vm.cb(data);
    }
}

module.exports = Contacts;
