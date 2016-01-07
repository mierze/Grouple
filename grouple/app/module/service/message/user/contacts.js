'use strict'
function Contacts(Getter) {
    this.get = get;
    
    return {
        get: this.get
    };
    
    function get(email, callback) {
        Getter.get('https://groupleapp.herokuapp.com/api/user/messages/contacts/' + email, callback);
    }
}

module.exports = Contacts;
