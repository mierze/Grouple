'use strict'
function EntityMessageGetter(Getter) {
    this.get = get;

    return {
        get: this.get
    };

    function get(id, type, callback) {
        Getter.get('http://groupleapp.herokuapp.com/api/' + type + '/message/messages/' + id, callback);
    }
}

module.exports = EntityMessageGetter;
