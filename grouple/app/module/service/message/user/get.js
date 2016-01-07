'use strict'
function Messages(Getter) {
    this.get = get;
    
    return {
        get: this.get
    };
    
    function get(params, callback) {
        Getter.get('http://groupleapp.herokuapp.com/api/' + type + '/messages/' + params.id + '/' + params.contact, callback);
    }
}

module.exports = Messages;
