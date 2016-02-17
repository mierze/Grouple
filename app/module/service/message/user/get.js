'use strict'
function UserMessageGetter(Getter) {
    this.get = get;

    return {
        get: this.get
    };

    function get(params, callback) {
        Getter.get('http://groupleapp.herokuapp.com/api/user/message/messages/' + params.email + '/' + params.contact, callback);
    }
}

module.exports = UserMessageGetter;
