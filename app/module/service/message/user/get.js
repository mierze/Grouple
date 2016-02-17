'use strict'
function UserMessageGetter(Getter) {
    this.get = get;

    return {
        get: this.get
    };

    function get(params, callback) {
        Getter.get('http://localhost:1337/api/user/message/messages/' + params.email + '/' + params.contact, callback);
    }
}

module.exports = UserMessageGetter;
